package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.initialization.SummingInitializer;
import com.ibm.hrl.scenoptic.domain.processors.ISingleValueProcessor;
import com.ibm.hrl.scenoptic.domain.processors.ProcessorCreator;
import com.ibm.hrl.scenoptic.utils.DefaultArrayList;

import java.util.List;

//@PlanningEntity
public class SummingShadowCellImpl<K extends Comparable<K>> extends SingleValueSummingShadowCell<K> {
	public SummingShadowCellImpl(K key,
	                             List<K> predecessors,
	                             int numberOfPredecessorLists,
	                             IncrementalPredecessorFormula<K> incrementalPredecessors,
	                             List<ProcessorCreator<ISingleValueProcessor>> processorConstructor) {
		super(key, predecessors, numberOfPredecessorLists, incrementalPredecessors,
				SummingShadowCellImpl::incrementalUpdateFormula, SummingShadowCellImpl::incrementalFreshFormula,
				new SummingInitializer<>(), processorConstructor);
		((SummingInitializer<SingleValueSummingShadowCell<K>>) (this.initializer)).setRef(this);
	}

	// FIXME!!!!!! need a formula to compute the term, also a selector for the registerKey from the prev/newValues
	public static Object incrementalUpdateFormula(Object subjectValue,
	                                              DefaultArrayList<Object> previousValues,
	                                              DefaultArrayList<Object> newValues,
	                                              DefaultArrayList<Object> previousScalars,
	                                              DefaultArrayList<Object> newScalars) {
		((SummingShadowCellImpl<?>) subjectValue).add(previousValues, -(int) (previousScalars.getOrDefault(0, 0)));
		((SummingShadowCellImpl<?>) subjectValue).add(newValues, (int) (newScalars.getOrDefault(0, 0)));
		return subjectValue;
	}

	public static Object incrementalFreshFormula(Object subjectValue,
	                                             DefaultArrayList<Object> newValues,
	                                             DefaultArrayList<Object> newScalars) {
		((SummingShadowCellImpl<?>) subjectValue).add(newValues, (int) (newScalars.getOrDefault(0, 0)));
		return subjectValue;
	}
}
