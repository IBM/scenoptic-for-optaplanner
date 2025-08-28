package com.ibm.hrl.test.sumif2arg.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.ibm.hrl.scenoptic.domain.PlanningCell;

@PlanningEntity
public class Sumif2argInt0To2048<K extends Comparable<K>> extends PlanningCell<K, Integer> {
	public Sumif2argInt0To2048() {
	}

	public Sumif2argInt0To2048(K key) {
		super(key);
	}

	@Override
	@PlanningVariable(valueRangeProviderRefs = "Int0To2048")
	public Integer getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Integer value) {
		super.setValue(value);
	}
}
