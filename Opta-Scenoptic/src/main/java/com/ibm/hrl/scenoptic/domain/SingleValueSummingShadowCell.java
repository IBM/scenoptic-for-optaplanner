package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalFreshFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalUpdateFormula;
import com.ibm.hrl.scenoptic.domain.initialization.IncrementalInitializer;
import com.ibm.hrl.scenoptic.domain.processors.ISingleValueProcessor;
import com.ibm.hrl.scenoptic.domain.processors.ProcessorCreator;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

// Note: the value of this cell as seen by OptaPlanner is this object or null
public abstract class SingleValueSummingShadowCell<K extends Comparable<K>>
		extends IncrementalCell<K, SingleValueSummingShadowCell<K>>
		implements ISummingCell<K, SingleValueSummingShadowCell<K>>, IDynamicDependentsCell<K>, ISpecialChangeIndicator {
	protected List<ProcessorCreator<ISingleValueProcessor>> processorCreators;
	protected Map<List<Object>, Set<K>> registered;
	@DeepPlanningClone
	protected Map<List<Object>, ISingleValueProcessor> processors = new HashMap<>();
	protected Set<List<Object>> changedRegisterKeys = new HashSet<>();
	// TODO: check this: Since this is not exposed to OptaPlanner, don't need workaround for initial values
	protected boolean enabled = false;
	protected long changeCounter = 0;
	protected long previousChangeCounter;

	public SingleValueSummingShadowCell(K key,
	                                    List<K> predecessors,
	                                    int numberOfPredecessorLists,
	                                    IncrementalPredecessorFormula<K> incrementalPredecessors,
	                                    IncrementalUpdateFormula incrementalUpdateFormula,
	                                    IncrementalFreshFormula incrementalFreshFormula,
	                                    IncrementalInitializer<SingleValueSummingShadowCell<K>> initializer,
	                                    List<ProcessorCreator<ISingleValueProcessor>> processorCreators) {
		super(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula,
				incrementalFreshFormula, initializer);
		this.processorCreators = processorCreators;
	}

	protected SingleValueSummingShadowCell() {
	}

	@Override
	public SingleValueSummingShadowCell<K> getValue() {
		if (enabled)
			return this;
		return null;
	}

	@Override
	public void setValue(SingleValueSummingShadowCell value) {
		super.setValue(value);
	}

	@Override
	public void register(List<Object> registerKey, K dependent) {
		Set<K> set = registered.computeIfAbsent(registerKey, k -> new HashSet<>());
		set.add(dependent);
//		System.out.println("~~~ Register " + registerKey);
	}

	@Override
	public void unregister(List<Object> registerKey, K dependent) {
		Set<K> set = registered.get(registerKey);
		set.remove(dependent);
		if (set.isEmpty())
			registered.remove(registerKey);
//		System.out.println("~~~ Unregister " + registerKey);
	}

	protected ISingleValueProcessor newProcessor() {
		try {
			return processorCreators.get(0).create(processorCreators.subList(1, processorCreators.size()));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void add(List<Object> registerKey, int value) {
		if (registerKey == null)
			throw new RuntimeException("Null key, value=" + value);
		processors.computeIfAbsent(registerKey, k -> newProcessor())
				.add(Collections.emptyList(), value);
		markChange(registerKey);
		System.out.println("~~~ Cell add " + value + " to " + registerKey + " value=" + processors.get(registerKey));
	}

	@Override
	public void add(List<Object> registerKey, double value) {
		processors.computeIfAbsent(registerKey, k -> newProcessor())
				.add(Collections.emptyList(), value);
		markChange(registerKey);
		System.out.println("~~~ Cell add " + value + " to " + registerKey);
	}

	@Override
	public void add(List<Object> registerKey, List<Object> dynamicKey, int value) {
		processors.computeIfAbsent(registerKey, k -> newProcessor())
				.add(dynamicKey, value);
		markChange(registerKey);
//		System.out.println("~~~ Cell add " + value + " to " + registerKey + "/" + dynamicKey);
	}

	@Override
	public void add(List<Object> registerKey, List<Object> dynamicKey, double value) {
		processors.computeIfAbsent(registerKey, k -> newProcessor())
				.add(dynamicKey, value);
		markChange(registerKey);
//		System.out.println("~~~ Cell add " + value + " to " + registerKey + "/" + dynamicKey);
	}

	@Override
	public int getInt(List<Object> registerKey, List<Object> dynamicKey) {
		final ISingleValueProcessor processor = processors.get(registerKey);
		if (processor == null)
			return 0;
		return processor.getInt(dynamicKey);
	}

	@Override
	public double getDouble(List<Object> registerKey, List<Object> dynamicKey) {
		final ISingleValueProcessor processor = processors.get(registerKey);
		if (processor == null)
			return 0.;
		return processor.getDouble(dynamicKey);
	}

	@Override
	public Stream<K> getDynamicDependents() {
		return changedRegisterKeys.stream()
				.flatMap(registeredKey -> registered.get(registeredKey).stream());
	}

	protected void markChange(List<Object> registerKey) {
		if (previousChangeCounter == changeCounter)
			changeCounter++;
		changedRegisterKeys.add(registerKey);
	}

	@Override
	public void startProcessing() {
		previousChangeCounter = changeCounter;
		System.out.println("~~~ Start proc " + changeCounter);
	}

	@Override
	public void endProcessing() {
		changedRegisterKeys.clear();
		System.out.println("~~~ End proc   " + changeCounter);
	}

	@Override
	public ISummingCell<?, SingleValueSummingShadowCell<K>> setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SingleValueSummingShadowCell<?> that = (SingleValueSummingShadowCell<?>) o;
		return enabled == that.enabled && changeCounter == that.changeCounter;
	}

	@Override
	public int hashCode() {
		return Objects.hash(enabled, changeCounter);
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() + " " + key + ">";
	}

	public String details() {
		var result = new StringBuilder("<" + getClass().getSimpleName() + " " + key + " ");
		String sep = "";
		for (Map.Entry<List<Object>, ISingleValueProcessor> entry : processors.entrySet()) {
			result.append(sep);
			sep = "; ";
			result.append(entry.getKey()).append("->").append(entry.getValue().valueString());
		}
		result.append(">");
		return result.toString();
	}

	@Override
	public boolean changedInLastCycle(Object oldValue) {
		return changeCounter != previousChangeCounter;
	}
}
