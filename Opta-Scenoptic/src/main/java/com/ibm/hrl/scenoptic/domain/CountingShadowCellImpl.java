package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalFreshFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalUpdateFormula;
import com.ibm.hrl.scenoptic.domain.initialization.SummingInitializer;
import com.ibm.hrl.scenoptic.domain.processors.ISingleValueProcessor;
import com.ibm.hrl.scenoptic.domain.processors.ProcessorCreator;
import com.ibm.hrl.scenoptic.utils.DefaultArrayList;
import org.optaplanner.core.api.domain.entity.PlanningEntity;

import java.util.List;

@PlanningEntity
public class CountingShadowCellImpl<K extends Comparable<K>> extends SingleValueSummingShadowCell<K> {
	public CountingShadowCellImpl(K key,
	                              List<K> predecessors,
	                              int numberOfPredecessorLists,
	                              IncrementalPredecessorFormula<K> incrementalPredecessors,
	                              List<ProcessorCreator<ISingleValueProcessor>> processorConstructor) {
		super(key, predecessors, numberOfPredecessorLists, incrementalPredecessors,
				new CountingUpdateFormula<>(), new CountingFreshFormula<>(),
				new SummingInitializer<>(), processorConstructor);
		((SummingInitializer<SingleValueSummingShadowCell<K>>) (this.initializer)).setRef(this);
		((CountingUpdateFormula<K>) (this.incrementalUpdateFormula)).setOwner(this);
		((CountingFreshFormula<K>) (this.incrementalFreshFormula)).setOwner(this);
	}

	public CountingShadowCellImpl() {
	}

	@Override
	public SingleValueSummingShadowCell<K> getValue() {
		return super.getValue();
	}

	private static class CountingUpdateFormula<K extends Comparable<K>> implements IncrementalUpdateFormula {
		CountingShadowCellImpl<K> owner;

		public void setOwner(CountingShadowCellImpl<K> owner) {
			assert this.owner == null;
			this.owner = owner;
		}

		public Object apply(Object subjectValue,
		                    DefaultArrayList<Object> previousValues,
		                    DefaultArrayList<Object> newValues,
		                    DefaultArrayList<Object> previousScalars,
		                    DefaultArrayList<Object> newScalars) {
			owner.add(previousValues, -1);
			owner.add(newValues, 1);
			return owner;
		}
	}


	private static class CountingFreshFormula<K extends Comparable<K>> implements IncrementalFreshFormula {
		CountingShadowCellImpl<K> owner;

		public void setOwner(CountingShadowCellImpl<K> owner) {
			assert this.owner == null;
			this.owner = owner;
		}

		public Object apply(Object subjectValue,
		                    DefaultArrayList<Object> newValues,
		                    DefaultArrayList<Object> newScalars) {
			owner.add(newValues, 1);
			return owner;
		}
	}
}