package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.domain.descriptors.Formula;
import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.utils.DefaultArrayList;

import java.util.List;
import java.util.stream.Stream;

public abstract class ComputedCell<K, T> extends Cell<K, T> {
	protected List<K> predecessors;
	protected Formula formula;
	protected boolean strict = true;

	// No-arg constructor required for OptaPlanner
	public ComputedCell() {
	}

	public ComputedCell(List<K> predecessors, Formula formula) {
		super();
		this.predecessors = predecessors;
		this.formula = formula;
	}

	public boolean isStrict() {
		return strict;
	}

	public Stream<K> getPredecessors() {
		return predecessors.stream()
				.flatMap(key -> (Stream<? extends K>) ((AbstractCellKey) key).getElements());
	}

	public Object applyFormula(DefaultArrayList<Object> values) {
		return formula.apply(values);
	}
}
