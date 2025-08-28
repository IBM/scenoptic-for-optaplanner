package com.ibm.hrl.test.counting.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.ibm.hrl.scenoptic.domain.PlanningCell;

@PlanningEntity
public class CountingInt0To11<K extends Comparable<K>> extends PlanningCell<K, int:Integer> {
	public CountingInt0To11() {
	}

	public CountingInt0To11(K key) {
		super(key);
	}

	@Override
	@PlanningVariable(valueRangeProviderRefs = "Int0To11")
	public int:Integer getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(int:Integer value) {
		super.setValue(value);
	}
}
