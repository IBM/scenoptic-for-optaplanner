package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.domain.descriptors.Formula;

import java.util.List;

public class ConditionalSumCell<K extends Comparable<K>, T> extends ShadowCell<K, T> implements IUpdatingCell<K, T> {
	protected ISummingCell<K, T> summingCell;

	public ConditionalSumCell(K key, List<K> predecessors, Formula formula, ISummingCell<K, T> summingCell) {
		super(key, predecessors, formula);
		this.summingCell = summingCell;
	}

	@Override
	public void changing(List<Object> previousValues, List<Object> newValues) {
		summingCell.unregister(previousValues, key);
		summingCell.register(newValues, key);
	}
}
