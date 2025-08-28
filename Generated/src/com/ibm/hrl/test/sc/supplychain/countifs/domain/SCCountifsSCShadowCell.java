package com.ibm.hrl.test.sc.supplychain.countifs.domain;

import java.util.List;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.algorithms.CellListener;
import com.ibm.hrl.scenoptic.domain.descriptors.Formula;
import com.ibm.hrl.scenoptic.domain.ShadowCell;

@PlanningEntity
public class SCCountifsSCShadowCell<T> extends ShadowCell<AbstractCellKey, T> {
	public SCCountifsSCShadowCell() {
	}

	public SCCountifsSCShadowCell(AbstractCellKey key, List<AbstractCellKey> predecessors, Formula formula) {
		super(key, predecessors, formula);
	}

	@Override
	@CustomShadowVariable(variableListenerClass = CellListener.class, sources = {
			@PlanningVariableReference(entityClass = SCCountifsSCInt1To1500.class, variableName = "value"),
			@PlanningVariableReference(entityClass = SCCountifsSCInt1To2000.class, variableName = "value") })
	public T getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(T value) {
		super.setValue(value);
	}
}
