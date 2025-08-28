package com.ibm.hrl.test.sc.supplychain.countifs.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.ibm.hrl.scenoptic.domain.PlanningCell;

@PlanningEntity
public class SCCountifsSCInt1To2000<K extends Comparable<K>> extends PlanningCell<K, Integer> {
	public SCCountifsSCInt1To2000() {
	}

	public SCCountifsSCInt1To2000(K key) {
		super(key);
	}

	@Override
	@PlanningVariable(valueRangeProviderRefs = "Int1To2000")
	public Integer getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Integer value) {
		super.setValue(value);
	}
}
