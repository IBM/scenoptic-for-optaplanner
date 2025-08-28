package com.ibm.hrl.scenoptic.algorithms;

import com.ibm.hrl.scenoptic.debug.DebugSpec;
import com.ibm.hrl.scenoptic.domain.Cell;
import com.ibm.hrl.scenoptic.domain.ComputedCell;
import com.ibm.hrl.scenoptic.domain.ICellWithInitialization;
import com.ibm.hrl.scenoptic.domain.ICellWithProcess;
import com.ibm.hrl.scenoptic.domain.IPrivateCell;
import com.ibm.hrl.scenoptic.domain.ISpecialChangeIndicator;
import com.ibm.hrl.scenoptic.domain.ISummingCell;
import com.ibm.hrl.scenoptic.domain.IUpdatingCell;
import com.ibm.hrl.scenoptic.domain.IncrementalCell;
import com.ibm.hrl.scenoptic.domain.PlanningCell;
import com.ibm.hrl.scenoptic.domain.ShadowCell;
import com.ibm.hrl.scenoptic.domain.SingleValueSummingShadowCell;
import com.ibm.hrl.scenoptic.domain.SpreadsheetProblem;
import com.ibm.hrl.scenoptic.keys.InitializationTriggerKey;
import com.ibm.hrl.scenoptic.utils.DefaultArrayList;
import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ibm.hrl.scenoptic.debug.DebugUtils.objectId;

abstract public class CellPropagator<K extends Comparable<K>> {
	protected ScoreDirector<SpreadsheetProblem<K>> scoreDirector;
	protected PriorityQueue<ComputedCell<K, ?>> agenda;
	protected Map<K, Optional<Object>> modified;
	protected Map<K, Integer> nonNullDelta;
	protected Map<K, Optional<Object>> originalValues;
	protected Map<K, Optional<Object>> updatedValues;
	protected Map<K, Set<Integer>> changedIncrementalPredecessors = new HashMap<>();
	protected boolean oneTimeInitializationDone = false;

	abstract public SpreadsheetProblem<K> getProblem();

	protected void queueDependents(K key) {
		final SpreadsheetProblem<K> problem = getProblem();
		// queue dependents
		final Stream<K> dependents = problem.dependentsOf(key);
		dependents.forEach(dep -> {
			final ComputedCell<K, ?> depCell = (ComputedCell<K, ?>) problem.findCell(dep, scoreDirector);
			if (!agenda.contains(depCell)) {
				agenda.add(depCell);
			}
		});
		final Stream<Pair<K, Integer>> incrementalDependents = problem.incrementalDependentsOf(key);
		incrementalDependents.forEach(incDep -> {
			final K incDepKey = incDep.getKey();
			final ComputedCell<K, ?> incDepCell = (ComputedCell<K, ?>) problem.findCell(incDepKey, scoreDirector);
			if (!agenda.contains(incDepCell))
				agenda.add(incDepCell);
			changedIncrementalPredecessors.computeIfAbsent(incDepKey, ignore -> new HashSet<>()).add(incDep.getValue());
		});
	}

	protected static boolean changed(Object oldValue, Object newValue) {
		if (oldValue == newValue && newValue instanceof ISpecialChangeIndicator)
			return ((ISpecialChangeIndicator) newValue).changedInLastCycle(oldValue);
		return !Objects.equals(oldValue, newValue);
	}

	public void propagate(CellListener<K> listener,
	                      Map<K, Optional<Object>> originalValues,
	                      Map<K, Optional<Object>> updatedValues,
	                      ScoreDirector<SpreadsheetProblem<K>> scoreDirector) {
		if (ignoreListener(listener, scoreDirector)) {
			getProblem().debug_println(DebugSpec.DebugLocation.LISTENER_WORKAROUND, "Ignoring listener " + objectId(listener));
			return;
		}
		this.originalValues = originalValues;
		this.updatedValues = updatedValues;
		this.scoreDirector = scoreDirector;
		final SpreadsheetProblem<K> problem = getProblem();
		problem.debug_println(DebugSpec.DebugLocation.CELL_PROP,
				"*** original values=" + originalValues + "; changed=" + updatedValues);
		final Map<K, Integer> index = problem.getTopologicalIndex();
		modified = new HashMap<>(
				problem.getCells().size() + problem.getDecisions().size() + problem.getIncrementals().size());
		nonNullDelta = new HashMap<>(problem.getIncrementals().size());
		agenda = new PriorityQueue<>(problem.getCells().size() + problem.getIncrementals().size(),
				Comparator.comparingInt(x -> index.get(x.getKey())));
		Optional<Entry<K, Optional<Object>>> trigger = updatedValues.entrySet().stream()
				.filter(entry -> entry.getKey() instanceof InitializationTriggerKey)
				.findFirst();
		trigger.ifPresent(this::initialize);
		for (K changedKey : updatedValues.keySet())
			if (!(changedKey instanceof InitializationTriggerKey))
				queueDependents(changedKey);
		while (agenda.size() > 0) {
			ComputedCell<K, ?> current = agenda.poll();
			if (current instanceof ICellWithProcess)
				((ICellWithProcess) current).startProcessing();
			final K currentKey = current.getKey();
			problem.debug_println(DebugSpec.DebugLocation.INC_PROP, "Pop: " + currentKey);
			final Object initialValue = current.getValue();
			DefaultArrayList<Object> values =
					getNewValues(current.getPredecessors());
			if (current instanceof IncrementalCell<?, ?>)
				propagateIncrementalCell(currentKey, (IncrementalCell<K, ?>) current, initialValue, values);
			else
				propagateRegularCell(currentKey, current, initialValue, values);
			if (current instanceof ICellWithProcess)
				((ICellWithProcess) current).endProcessing();
		}
		reportChangedValues();
		this.scoreDirector = null;
		this.originalValues = null;
		this.updatedValues = null;
		agenda = null;
		modified = null;
		nonNullDelta = null;
		changedIncrementalPredecessors.clear();
	}

	private void propagateIncrementalCell(K currentKey,
	                                      IncrementalCell<K, ?> current,
	                                      Object initialValue,
	                                      DefaultArrayList<Object> values) {
		final SpreadsheetProblem<K> problem = getProblem();
		Set<K> changedKeys = new HashSet<>(originalValues.keySet());
		changedKeys.addAll(modified.keySet());
		changedKeys.retainAll(current.getPredecessors().collect(Collectors.toSet()));
		if (changedKeys.isEmpty())
			propagateIncrementally(currentKey, current, initialValue, values);
		else {
			// a scalar argument has changed, compute from scratch
			Object dynamicValue = null;
			Object constantValue = null;
			current.resetNonNullInput();
			for (int i = 0; i < current.numberOfIncrementalPredecessors(); i++) {
				problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
						">>>S   Cell=" + current);
				DefaultArrayList<Object> newValues =
						getNewValues(Arrays.stream(current.getIncrementalPredecessors(currentKey, i)));
				problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
						">>>S   values=" + newValues);
				if (newValues.stream().allMatch(Objects::isNull))
					continue;
				current.addNonNullInput(1);
				if (hasDynamicInputs(current, i))
					dynamicValue = current.applyIncrementalFreshFormula(dynamicValue, newValues, values);
				else
					constantValue = current.applyIncrementalFreshFormula(constantValue, newValues, values);
			}
			current.resetInitialValue(constantValue);
			problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
					"===S Setting initial value of " + current.getKey() + " to " + constantValue);
			current.unsafeSetValue(dynamicValue);
			Object newValue = dynamicValue;
			if (problem.getInitializationTrigger().getValue() != null) {
				newValue = current.applyInitialValue();
				current.addNonNullInput(1);
			}
			if (changed(current.getValue(), newValue)) {
				modified.put(current.getKey(), Optional.ofNullable(newValue));
				queueDependents(current.getKey());
				problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
						"===S Changing " + current.getKey() + " to " + newValue);
			}
		}
	}

	private void propagateIncrementally(K currentKey,
	                                    IncrementalCell<K, ?> current,
	                                    Object initialValue,
	                                    DefaultArrayList<Object> newScalars) {
		// Propagate incremental cells whose scalar predecessors haven't changed
		final SpreadsheetProblem<K> problem = getProblem();
		Set<Integer> changedEntries = changedIncrementalPredecessors.get(currentKey);
		Object incrementalValue = initialValue;
		int delta = 0;
		for (int entryIndex : changedEntries) {
			K[] incrementalPredecessors = current.getIncrementalPredecessors(currentKey, entryIndex);
			DefaultArrayList<Object> previousScalars =
					getPreviousValues(current.getPredecessors());
			DefaultArrayList<Object> previousValues =
					getPreviousValues(Stream.of(incrementalPredecessors));
			DefaultArrayList<Object> newValues =
					getNewValues(Stream.of(incrementalPredecessors));
			if (problem.debug(DebugSpec.DebugLocation.INC_PROP))
				System.out.print("key=" + currentKey + "; index=" + entryIndex + "; current=" + incrementalValue
						+ "; prev=" + previousValues + "; new=" + newValues);
			boolean prevAllNone = previousValues.stream().allMatch(Objects::isNull);
			boolean newAllNone = newValues.stream().allMatch(Objects::isNull);
			if (prevAllNone & !newAllNone)
				delta += 1;
			else if (!prevAllNone & newAllNone)
				delta -= 1;
			incrementalValue = current.applyIncrementalUpdateFormula(
					incrementalValue, previousValues, newValues, previousScalars, newScalars);
			problem.debug_println(DebugSpec.DebugLocation.INC_PROP, "; final=" + incrementalValue);
		}
		if (problem.debug(DebugSpec.DebugLocation.INC_PROP) && delta != 0) {
			System.out.print("ΔΔΔ key=" + currentKey + "; delta=" + delta);
			if (current.getNonNullInputs() + delta == 0)
				System.out.println("; setting to null");
			else
				System.out.println();
		}
		nonNullDelta.put(currentKey, delta);
		if (current.getNonNullInputs() + delta == 0)
			incrementalValue = null;
		if (changed(initialValue, incrementalValue)) {
			queueDependents(currentKey);
			modified.put(currentKey, Optional.ofNullable(incrementalValue));
		}
	}

	private void initialize(Entry<K, Optional<Object>> trigger) {
		final SpreadsheetProblem<K> problem = getProblem();
		K changedKey = trigger.getKey();
		Integer triggerValue = (Integer) trigger.getValue().orElse(null);
		// Special case: Initialize incremental variables
		Integer oldTriggerValue = (Integer) originalValues.get(changedKey).orElse(null);
		problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
				">>> Starting incremental initialization: " + triggerValue);
		// This workaround is needed because (1) initialization of shadow variables is not supported;
		// (2) it is difficult to generate the default construction-heuristic phase in order to add a
		// custom phase.
		if (triggerValue == null) {
			if (oldTriggerValue != null)
				// retracting, reduce non-null-delta
				for (IncrementalCell<K, ?> inc : problem.getIncrementals()) {
					inc.addNonNullInput(-1);
					inc.withdrawInitialValue();
				}
		} else if (oldTriggerValue == null) {
			// do initialization
			for (Iterator<? extends ICellWithInitialization<K, ?>> it = problem.getCellsWithInitialization().iterator(); it.hasNext(); ) {
				ICellWithInitialization<K, ?> inc = it.next();
				inc.addNonNullInput(1);
				Object incrementalValue = inc.getValue();
				if (inc.getInitialValue() == null) {
					for (int i = 0; i < inc.numberOfIncrementalPredecessors(); i++) {
						if (hasDynamicInputs(inc, i))
							continue;
						problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
								">>>   Cell=" + inc);
						DefaultArrayList<Object> scalarValues =
								getNewValues(inc.getPredecessors());
						DefaultArrayList<Object> newValues =
								getNewValues(Arrays.stream(inc.getIncrementalPredecessors(inc.getKey(), i)));
						problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
								">>>   values=" + newValues);
						if (newValues.stream().allMatch(Objects::isNull))
							continue;
						incrementalValue = inc.applyIncrementalFreshFormula(incrementalValue, newValues, scalarValues);
					}
					inc.setInitialValue(incrementalValue);
					problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
							"=== Setting initial value of " + inc.getKey() + " to " + incrementalValue);
				}
				Object newValue = inc.applyInitialValue();
				if (changed(inc.getValue(), newValue)) {
					modified.put(inc.getKey(), Optional.ofNullable(newValue));
					queueDependents(inc.getKey());
					problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
							"=== Changing " + inc.getKey() + " to " + newValue);
				}
			}
		}
		queueDependents(changedKey);
		doOneTimeInitialization();
	}

	private boolean hasDynamicInputs(ICellWithInitialization<K, ?> inc, int i) {
		return Arrays
				.stream(inc.getIncrementalPredecessors(inc.getKey(), i))
				.anyMatch(pred -> pred instanceof ShadowCell || pred instanceof PlanningCell);
	}

	public void doOneTimeInitialization() {
		if (oneTimeInitializationDone)
			return;
		oneTimeInitializationDone = true;
		final SpreadsheetProblem<K> problem = getProblem();
		for (ISummingCell<K, ?> isum : problem.getSummingCells()) {
			// TODO: support other kinds of summing cells
			SingleValueSummingShadowCell<K> sum = (SingleValueSummingShadowCell<K>) isum;
			Object incrementalValue = sum.getValue();
			for (int i = 0; i < sum.numberOfIncrementalPredecessors(); i++) {
				if (hasDynamicInputs(sum, i))
					continue;
				problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
						">>>   Cell=" + sum);
				DefaultArrayList<Object> scalarValues =
						getNewValues(sum.getPredecessors());
				DefaultArrayList<Object> newValues =
						getNewValues(Arrays.stream(sum.getIncrementalPredecessors(sum.getKey(), i))
						);
				problem.debug_println(DebugSpec.DebugLocation.INC_PHASE,
						">>>   values=" + newValues);
				incrementalValue = sum.applyIncrementalFreshFormula(incrementalValue, newValues, scalarValues);
//				System.out.print("Initialized: ");
//				System.out.println(sum.details());
			}
			isum.setInitialValue(incrementalValue);
		}
	}

	private DefaultArrayList<Object> getNewValues(Stream<? extends K> keys) {
		final SpreadsheetProblem<K> problem = getProblem();
		return keys.map(prec -> {
					// Note: not using getOrDefault, to prevent unnecessary computation of the default
					final Optional<Object> optionalValue = modified.get(prec);
					if (optionalValue == null)
						return problem.findCell(prec, scoreDirector).getValue();
					return optionalValue.orElse(null);
				})
				.collect(Collectors.toCollection(DefaultArrayList::new));
	}

	private DefaultArrayList<Object> getPreviousValues(Stream<? extends K> keys) {
		final SpreadsheetProblem<K> problem = getProblem();
		return keys.map(prec -> {
			Optional<Object> originalValue;
			Object value;
			originalValue = originalValues.get(prec);
			if (originalValue == null) {
				value = problem.findCell(prec, scoreDirector).getValue();
				problem.debug_println(DebugSpec.DebugLocation.INC_PROP,
						"@@@ using cell value " + prec + "=" + value);
			} else {
				value = originalValue.orElse(null);
				problem.debug_println(DebugSpec.DebugLocation.INC_PROP,
						"@@@ using original value " + prec + "=" + originalValue);
			}
			return value;
		}).collect(Collectors.toCollection(DefaultArrayList::new));
	}

	private void propagateRegularCell(K currentKey,
	                                  ComputedCell<K, ?> current,
	                                  Object initialValue,
	                                  DefaultArrayList<Object> values) {
		// Propagate non-incremental cells (full computation)
		if (current instanceof IUpdatingCell)
			((IUpdatingCell<K, ?>) current).changing(getPreviousValues(current.getPredecessors()), values);
		if (!(current.isStrict() && values.stream().anyMatch(Objects::isNull))) {
			Object newValue = current.applyFormula(values);
			if (changed(initialValue, newValue)) {
				queueDependents(currentKey);
				modified.put(currentKey, Optional.ofNullable(newValue));
			}
		} else {
			// Strict function with missing args: newValue is null
			if (initialValue != null) {
				queueDependents(currentKey);
				modified.put(currentKey, Optional.empty());
			}
		}
	}

	private void reportChangedValues() {
		final SpreadsheetProblem<K> problem = getProblem();
		for (Entry<K, Optional<Object>> entry : modified.entrySet()) {
			K cellKey = entry.getKey();
			@SuppressWarnings("rawtypes")
			Cell cell = problem.findCell(cellKey, scoreDirector);
			if (scoreDirector != null && !(cell instanceof IPrivateCell))
				scoreDirector.beforeVariableChanged(cell, "value");
			if (cell instanceof IncrementalCell) {
				Integer delta = nonNullDelta.get(cellKey);
				if (delta != null) {
					((IncrementalCell<?, ?>) cell).addNonNullInput(delta);
				}
			}
			cell.setValue(entry.getValue().orElse(null));
			if (scoreDirector != null && !(cell instanceof IPrivateCell))
				scoreDirector.afterVariableChanged(cell, "value");
		}
	}

	// WORKAROUND for case when two different CellListener objects point to the same ScoreDirector and solution objects
	protected static IdentityHashMap<ScoreDirector<? extends SpreadsheetProblem>, CellListener> listenerMap =
			new IdentityHashMap<>();

	protected boolean ignoreListener(CellListener<K> listener,
	                                 ScoreDirector<? extends SpreadsheetProblem<K>> scoreDirector) {
		CellListener<K> existing = listenerMap.get(scoreDirector);
		if (existing == null) {
			listenerMap.put(scoreDirector, listener);
			return false;
		}
		return existing != listener;
	}
}
