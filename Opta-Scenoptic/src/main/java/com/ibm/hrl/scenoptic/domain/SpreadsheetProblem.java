package com.ibm.hrl.scenoptic.domain;

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.ibm.hrl.scenoptic.debug.DebugSpec;
import com.ibm.hrl.scenoptic.keys.InitializationTriggerKey;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Map.entry;

@PlanningSolution
public abstract class SpreadsheetProblem<K extends Comparable<K>> {
	@PlanningEntityCollectionProperty
	private List<? extends PlanningCell<K, ?>> decisions;
	@PlanningEntityCollectionProperty
	private List<? extends ShadowCell<K, ?>> cells;
	@PlanningEntityCollectionProperty
	private List<? extends ISummingCell<K, ?>> summingCells;
	@ProblemFactCollectionProperty
	protected List<? extends InputCell<K, ?>> inputs;
	@PlanningEntityCollectionProperty
	private List<? extends IncrementalCell<K, ?>> incrementals;
	@PlanningEntityProperty
	private InitializationTriggerCell initializationTrigger = new InitializationTriggerCell(InitializationTriggerKey.SINGLETON);

	private DebugSpec debug;

	// pre-computed fields
	protected Map<K, Collection<K>> dependents;
	protected Map<K, Collection<Pair<K, Integer>>> incrementalDependents;

	protected Map<K, Integer> decisionMap;
	protected Map<K, Integer> cellMap;
	protected Map<K, Integer> inputMap;
	protected Map<K, Integer> initializedMap;
	protected Map<K, Integer> incrementalMap;
	protected Map<K, Integer> summingMap;
	// Maps a cell to its kind (0=decision, 1=shadow, 2=input, 3=initialized, 4=incremental, 5=summing cell)
	protected Map<K, Integer> cellToKind;
	protected Map<K, Integer>[] allMaps;
	protected List<? extends Cell<K, ?>>[] allLists;
	protected Map<K, Integer> topologicalIndex;

	// No-arg constructor required for OptaPlanner
	public SpreadsheetProblem() {
	}

	public SpreadsheetProblem(List<? extends PlanningCell<K, ?>> decisions,
	                          List<? extends ShadowCell<K, ?>> cells,
	                          List<? extends InputCell<K, ?>> inputs,
	                          List<? extends IncrementalCell<K, ?>> incrementals,
	                          List<? extends ISummingCell<K, ?>> summingCells) {
		this.setDecisions(decisions);
		this.setCells(cells);
		this.inputs = inputs;
		this.incrementals = incrementals;
		this.summingCells = summingCells;
		preCompute();
	}

	public Map<K, Integer> getTopologicalIndex() {
		return topologicalIndex;
	}

	public Stream<K> dependentsOf(K key) {
		final Collection<K> deps = dependents.get(key);
		if (deps == null)
			return Stream.empty();
		return deps.stream();
	}

	public Stream<Pair<K, Integer>> incrementalDependentsOf(K key) {
		final Collection<Pair<K, Integer>> deps = incrementalDependents.get(key);
		if (deps == null)
			return Stream.empty();
		return deps.stream();
	}

	public List<? extends PlanningCell<K, ?>> getDecisions() {
		return decisions;
	}

	public void setDecisions(List<? extends PlanningCell<K, ?>> decisions) {
		this.decisions = decisions;
	}

	public List<? extends ShadowCell<K, ?>> getCells() {
		return cells;
	}

	public void setCells(List<? extends ShadowCell<K, ?>> cells) {
		this.cells = cells;
	}

	public List<? extends ISummingCell<K, ?>> getSummingCells() {
		return summingCells;
	}

	public void setSummingCells(List<? extends ISummingCell<K, ?>> summingCells) {
		this.summingCells = summingCells;
	}

	public List<? extends IncrementalCell<K, ?>> getIncrementals() {
		return incrementals;
	}

	public Stream<? extends ICellWithInitialization<K, ?>> getCellsWithInitialization() {
		return Stream.concat(incrementals.stream(), summingCells.stream());
	}

	public void setIncrementals(List<? extends IncrementalCell<K, ?>> incrementals) {
		this.incrementals = incrementals;
	}

	protected void putCellKind(K key, int kind) {
		if (cellToKind.get(key) != null)
			throw new RuntimeException("Multiple definitions for cell: " + key);
		cellToKind.put(key, kind);
	}

	protected void preCompute() {
		decisionMap = IntStream.range(0, getDecisions().size())
				.boxed()
				.collect(Collectors.toMap(i -> getDecisions().get(i).getKey(), Function.identity()));
		cellMap = IntStream.range(0, getCells().size())
				.boxed()
				.collect(Collectors.toMap(i -> getCells().get(i).getKey(), Function.identity()));
		inputMap = IntStream.range(0, inputs.size())
				.boxed()
				.collect(Collectors.toMap(i -> inputs.get(i).getKey(), Function.identity()));
		incrementalMap = IntStream.range(0, incrementals.size())
				.boxed()
				.collect(Collectors.toMap(i -> incrementals.get(i).getKey(), Function.identity()));
		summingMap = IntStream.range(0, summingCells.size())
				.boxed()
				.collect(Collectors.toMap(i -> summingCells.get(i).getKey(), Function.identity()));
		cellToKind = new HashMap<>();
		getDecisions().forEach(c -> putCellKind(c.getKey(), 0));
		getCells().forEach(c -> putCellKind(c.getKey(), 1));
		inputs.forEach(c -> putCellKind(c.getKey(), 2));
		getIncrementals().forEach(c -> putCellKind(c.getKey(), 3));
		getSummingCells().forEach(c -> putCellKind(c.getKey(), 4));
		allLists = new List[]{getDecisions(), getCells(), inputs, getIncrementals(), getSummingCells()};
		allMaps = new Map[]{decisionMap, cellMap, inputMap, incrementalMap, summingMap};
		dependents = new HashMap<>();
		for (Entry<K, Integer> entry : cellMap.entrySet()) {
			final K current = entry.getKey();
			getCells().get(entry.getValue()).getPredecessors()
					.forEach(pred -> dependents.computeIfAbsent(pred, x -> new HashSet<>()).add(current));
		}
		incrementalDependents = new HashMap<>();
		incrementalMap.forEach((current, value) -> addIncrementalDependents(current, value, incrementals));
		summingMap.forEach((current, value) -> addIncrementalDependents(current, value, summingCells));
		preparePropagationInfo();
	}

	private void addIncrementalDependents(K current, Integer value,
	                                      List<? extends ICellWithIncrementalPredecessors<K>> incrementalCellList) {
		final ICellWithIncrementalPredecessors<K> incremental = incrementalCellList.get(value);
		final K incKey = incremental.getKey();
		for (int i = 0; i < incremental.numberOfIncrementalPredecessors(); i++) {
			K[] predecessors = incremental.getIncrementalPredecessors(incKey, i);
			for (K predecessor : predecessors) {
				incrementalDependents.computeIfAbsent(predecessor, x -> new HashSet<>())
						.add(new ImmutablePair<>(current, i));
			}
		}
	}

	public void preparePropagationInfo() {
		List<?> constantNames = inputs.stream().map(Cell::getKey)
				.collect(Collectors.toList());
		Map<K, Set<K>> dynamicDependents = dependents.entrySet().stream()
				.filter(entry -> !constantNames.contains(entry.getKey()))
				.map(entry -> entry(entry.getKey(),
						entry.getValue().stream()
								.filter(value -> !constantNames.contains(value))
								.collect(Collectors.toSet())))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		incrementalDependents.forEach((key, value) ->
				dynamicDependents.merge(key, value.stream().map(Pair::getKey).collect(Collectors.toSet()), Sets::union));
		List<K> topologicalOrder = topologicalSort(dynamicDependents,
				decisions.stream().map(Cell::getKey).collect(Collectors.toList()));
		topologicalIndex = Streams.mapWithIndex(topologicalOrder.stream(), Map::entry)
				.collect(Collectors.toMap(Entry::getKey, entry -> (int) (long) entry.getValue()));
//		System.out.println(topologicalOrder);
//		System.out.println(topologicalIndex);
	}

	public List<K> topologicalSort(Map<K, Set<K>> edges, Collection<K> initialCandidates) {
		List<K> result = new ArrayList<>(edges.keySet().size());
		Map<K, Integer> incoming = new HashMap<>();
		edges.keySet().forEach(key -> incoming.put(key, 0));
		edges.forEach((key, value) -> value
				.forEach(follower -> incoming.put(follower, incoming.getOrDefault(follower, 0) + 1)));
		Set<K> candidates = new HashSet<>(initialCandidates);
		incoming.entrySet().stream()
				.filter(entry -> entry.getValue() == 0)
				.forEach(entry -> candidates.add(entry.getKey()));
		while (!candidates.isEmpty()) {
			K current = candidates.iterator().next();
			candidates.remove(current);
			result.add(current);
			final Collection<K> currentDependents = edges.get(current);
			if (currentDependents != null)
				currentDependents
						.forEach(follower -> {
							final int incoming_after = incoming.get(follower) - 1;
							incoming.put(follower, incoming_after);
							if (incoming_after == 0)
								candidates.add(follower);
						});
		}
		return result;
	}

	public PlanningCell<K, ?> getDecision(K key) {
		final Integer index = decisionMap.get(key);
		if (index == null)
			return null;
		return getDecisions().get(index);
	}

	public ShadowCell<K, ?> getCell(K key) {
		final Integer index = cellMap.get(key);
		if (index == null)
			return null;
		return getCells().get(index);
	}

	public IncrementalCell<?, ?> getIncremental(K key) {
		final Integer index = incrementalMap.get(key);
		if (index == null)
			return null;
		return getIncrementals().get(index);
	}

	public List<ShadowCell<K, ?>> getCells(Collection<K> keys) {
		return keys.stream().map(this::getCell).collect(Collectors.toList());
	}

	public Cell<K, ?> findCell(K key, ScoreDirector<SpreadsheetProblem<K>> scoreDirector) {
		Integer kind = cellToKind.get(key);
		if (kind == null)
			return null;
		Integer index = allMaps[kind].get(key);
		if (index == null)
			return null;
		Cell<K, ?> result = allLists[kind].get(index);
		if (scoreDirector == null || result == null || result instanceof IPrivateCell)
			return result;
		else
			return scoreDirector.lookUpWorkingObject(result);
	}

	public List<? extends Cell<K, ?>> findCells(Collection<K> keys, boolean needValues,
	                                            ScoreDirector<SpreadsheetProblem<K>> scoreDirector) {
		List<? extends Cell<K, ?>> result = keys.stream().map(key -> findCell(key, scoreDirector)).collect(Collectors.toList());
		if (needValues && result.stream().anyMatch(cell -> cell == null || cell.getValue() == null))
			return null;
		return result;
	}

	public void printCells(List<? extends Cell<K, ?>> cells) {
		for (Cell<K, ?> cell : cells) {
			System.out.println(cell.getKey() + ": " + cell.getValue());
		}
	}

	public void printResults() {
		printCells(getDecisions());
	}

	public void printShadows() {
		printCells(getCells());
		printCells(getIncrementals());
	}

	abstract public Score getScore();

	abstract public void setScore(Score score);

	@ValueRangeProvider(id = "*Singleton-provider*")
	public ValueRange<Integer> getSingletonRange() {
		return ValueRangeFactory.createIntValueRange(0, 1);
	}

	public DebugSpec getDebug() {
		return debug;
	}

	public void setDebug(DebugSpec debug) {
		this.debug = debug;
	}

	public boolean debug(DebugSpec.DebugLocation current) {
		return debug.debug(current);
	}

	public void debug_println(DebugSpec.DebugLocation current, String text) {
		debug.println(current, text);
	}

	public void debug_println(DebugSpec.DebugLocation[] current, String text) {
		debug.println(current, text);
	}

	public InitializationTriggerCell getInitializationTrigger() {
		return initializationTrigger;
	}

	public void setInitializationTrigger(InitializationTriggerCell initializationTrigger) {
		this.initializationTrigger = initializationTrigger;
	}
}
