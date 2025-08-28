package com.ibm.hrl.test.scalars.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.ibm.hrl.scenoptic.domain.PlanningCell;

@PlanningEntity
public class ScalarsInt0To11<K extends Comparable<K>> extends PlanningCell<K, Integer> {
	public ScalarsInt0To11() {
	}

	public ScalarsInt0To11(K key) {
		super(key);
	}

	@Override
	@PlanningVariable(valueRangeProviderRefs = "Int0To11")
	public Integer getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Integer value) {
		super.setValue(value);
	}
}
