package com.ibm.hrl.test.aggregate.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.ibm.hrl.scenoptic.domain.PlanningCell;

@PlanningEntity
public class AggregateInt0To3<K extends Comparable<K>> extends PlanningCell<K, Integer> {
	public AggregateInt0To3() {
	}

	public AggregateInt0To3(K key) {
		super(key);
	}

	@Override
	@PlanningVariable(valueRangeProviderRefs = "Int0To3")
	public Integer getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Integer value) {
		super.setValue(value);
	}
}
