package com.ibm.hrl.test.scalars.domain;

import java.util.List;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.algorithms.CellListener;
import com.ibm.hrl.scenoptic.domain.IncrementalCell;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalUpdateFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalFreshFormula;
import com.ibm.hrl.scenoptic.domain.InitializationTriggerCell;
import com.ibm.hrl.scenoptic.domain.initialization.IncrementalInitializer;

@PlanningEntity
public class ScalarsIncrementalCell<T> extends IncrementalCell<AbstractCellKey, T> {
	public ScalarsIncrementalCell() {
	}

	public ScalarsIncrementalCell(AbstractCellKey key, List<AbstractCellKey> predecessors, int numberOfPredecessorLists, IncrementalPredecessorFormula<AbstractCellKey> incrementalPredecessors,
                           IncrementalUpdateFormula incrementalUpdateFormula, IncrementalFreshFormula incrementalFreshFormula,
                           IncrementalInitializer<T> initializer) {
		super(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula, initializer);
	}

	@Override
	@CustomShadowVariable(variableListenerClass = CellListener.class, sources = {
			@PlanningVariableReference(entityClass = ScalarsInt0To11.class, variableName = "value"),
			@PlanningVariableReference(entityClass = InitializationTriggerCell.class, variableName = "value") })
	public T getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(T value) {
		super.setValue(value);
	}
}
