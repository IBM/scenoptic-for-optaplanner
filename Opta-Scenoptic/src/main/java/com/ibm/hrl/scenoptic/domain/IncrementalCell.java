package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalFreshFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalUpdateFormula;
import com.ibm.hrl.scenoptic.domain.initialization.IncrementalInitializer;
import com.ibm.hrl.scenoptic.utils.DefaultArrayList;
import org.optaplanner.core.api.domain.entity.PlanningEntity;

import java.util.List;

@PlanningEntity
public abstract class IncrementalCell<K extends Comparable<K>, T> extends ShadowCell<K, T>
		implements ICellWithIncrementalPredecessors<K>,
		ICellWithInitialization<K, T> {
	// number of predecessor lists that are completely unavailable (all null), including initialization trigger
	protected int nonNullInputs = 0;
	protected IncrementalPredecessorFormula<K> incrementalPredecessors;
	protected IncrementalUpdateFormula incrementalUpdateFormula;
	protected IncrementalFreshFormula incrementalFreshFormula;
	protected IncrementalInitializer<T> initializer;
	protected T initialValue;
	protected int numberOfPredecessorLists;

	// No-arg constructor required for OptaPlanner
	public IncrementalCell() {
	}

	public IncrementalCell(K key, List<K> predecessors, int numberOfPredecessorLists, IncrementalPredecessorFormula<K> incrementalPredecessors,
	                       IncrementalUpdateFormula incrementalUpdateFormula, IncrementalFreshFormula incrementalFreshFormula,
	                       IncrementalInitializer<T> initializer) {
		super(key, predecessors, null);
		this.numberOfPredecessorLists = numberOfPredecessorLists;
		this.incrementalPredecessors = incrementalPredecessors;
		this.incrementalUpdateFormula = incrementalUpdateFormula;
		this.incrementalFreshFormula = incrementalFreshFormula;
		this.initializer = initializer;
		this.setKey(key);
	}

	public IncrementalCell(K key, T value, List<K> predecessors, int numberOfPredecessorLists, IncrementalPredecessorFormula<K> incrementalPredecessors,
	                       IncrementalUpdateFormula incrementalUpdateFormula, IncrementalFreshFormula incrementalFreshFormula,
	                       IncrementalInitializer<T> initializer) {
		this(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula,
				initializer);
		this.setValue(value);
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void unsafeSetValue(Object value) {
		this.value = (T) value;
	}

	public int getNonNullInputs() {
		return nonNullInputs;
	}

	public void addNonNullInput(int delta) {
		nonNullInputs += delta;
	}

	public void resetNonNullInput() {
		nonNullInputs = 0;
	}

	public T getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(Object initialValue) {
		if (this.initialValue != null)
			throw new RuntimeException("Attempt to modify initial value of incremental cell " + key);
		this.initialValue = (T) initialValue;
	}

	public void resetInitialValue(Object initialValue) {
		this.initialValue = (T) initialValue;
	}

	public T applyInitialValue() {
		value = initializer.addValue(value, initialValue);
		return value;
	}

	public T withdrawInitialValue() {
		value = initializer.removeValue(value, initialValue);
		return value;
	}

	@Override
	public int numberOfIncrementalPredecessors() {
		return numberOfPredecessorLists;
	}

	@Override
	public K[] getIncrementalPredecessors(K key, int i) {
		return incrementalPredecessors.apply(key, i);
	}

	public Object applyIncrementalUpdateFormula(
			Object subjectValue, DefaultArrayList<Object> previousValues, DefaultArrayList<Object> newValues,
			DefaultArrayList<Object> previousScalars, DefaultArrayList<Object> newScalars) {
		return incrementalUpdateFormula.apply(subjectValue, previousValues, newValues, previousScalars, newScalars);
	}

	public Object applyIncrementalFreshFormula(Object subjectValue, DefaultArrayList<Object> newValues, DefaultArrayList<Object> newScalars) {
		return incrementalFreshFormula.apply(subjectValue, newValues, newScalars);
	}
}
