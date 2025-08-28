package com.ibm.hrl.test.simplified.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.ibm.hrl.scenoptic.domain.PlanningCell;

@PlanningEntity
public class SimplifiedInt0To11<K extends Comparable<K>> extends PlanningCell<K, Integer> {
	public SimplifiedInt0To11() {
	}

	public SimplifiedInt0To11(K key) {
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
