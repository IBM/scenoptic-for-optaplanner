package com.ibm.hrl.test.sumif2arg.domain;

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
public class Sumif2argShadowCell<T> extends ShadowCell<AbstractCellKey, T> {
	public Sumif2argShadowCell() {
	}

	public Sumif2argShadowCell(AbstractCellKey key, List<AbstractCellKey> predecessors, Formula formula) {
		super(key, predecessors, formula);
	}

	@Override
	@CustomShadowVariable(variableListenerClass = CellListener.class, sources = {
			@PlanningVariableReference(entityClass = Sumif2argInt0To2048.class, variableName = "value") })
	public T getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(T value) {
		super.setValue(value);
	}
}
