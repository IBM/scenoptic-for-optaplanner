package com.ibm.hrl.scenoptic.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@PlanningEntity
public class PlanningCell<K extends Comparable<K>, T> extends Cell<K, T> {
	@PlanningId
	protected K key;
	protected T value;

	// No-arg constructor required for OptaPlanner
	public PlanningCell() {
	}

	public PlanningCell(K key) {
		this.setKey(key);
	}

	public PlanningCell(K key, T value) {
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
//		System.out.println("SETTING " + key + " to " + value);
		this.value = value;
	}
}
