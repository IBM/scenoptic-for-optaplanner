package com.ibm.hrl.test.counting.domain;

import com.ibm.hrl.scenoptic.algorithms.CellListener;
import com.ibm.hrl.scenoptic.domain.CountingShadowCellImpl;
import com.ibm.hrl.scenoptic.domain.SingleValueSummingShadowCell;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.processors.ISingleValueProcessor;
import com.ibm.hrl.scenoptic.domain.processors.ProcessorCreator;
import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import java.util.List;

@PlanningEntity
public class CountingCountingShadowCell<T> extends CountingShadowCellImpl<AbstractCellKey> {
	public CountingCountingShadowCell() {
	}

	public CountingCountingShadowCell(AbstractCellKey key, List<AbstractCellKey> predecessors, int numberOfPredecessorLists, IncrementalPredecessorFormula<AbstractCellKey> incrementalPredecessors, List<ProcessorCreator<ISingleValueProcessor>> processorConstructor) {
		super(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, processorConstructor);
	}

	@Override
	@CustomShadowVariable(variableListenerClass = CellListener.class, sources = {
			@PlanningVariableReference(entityClass = CountingInt0To11.class, variableName = "value") })
	public SingleValueSummingShadowCell<AbstractCellKey> getValue() {
		return super.getValue();
	}
}
