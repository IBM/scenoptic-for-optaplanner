package com.ibm.hrl.test.sc.supplychain.sumif.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.ibm.hrl.scenoptic.domain.PlanningCell;

@PlanningEntity
public class SCSumifSCInt1To1500<K extends Comparable<K>> extends PlanningCell<K, Integer> {
	public SCSumifSCInt1To1500() {
	}

	public SCSumifSCInt1To1500(K key) {
		super(key);
	}

	@Override
	@PlanningVariable(valueRangeProviderRefs = "Int1To1500")
	public Integer getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Integer value) {
		super.setValue(value);
	}
}
