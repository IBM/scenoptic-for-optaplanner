package com.ibm.hrl.scenoptic.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@PlanningEntity
public class InputCell<K extends Comparable<K>, T> extends Cell<K, T> {
	@PlanningId
	protected K key;
	protected T value;

	// No-arg constructor required for OptaPlanner
	public InputCell() {
	}

	public InputCell(K key) {
		this.setKey(key);
	}

	public InputCell(K key, T value) {
		this.setKey(key);
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
}
