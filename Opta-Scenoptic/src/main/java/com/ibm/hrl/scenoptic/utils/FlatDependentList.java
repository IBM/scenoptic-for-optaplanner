package com.ibm.hrl.scenoptic.utils;

import java.util.AbstractList;
import java.util.stream.Stream;

public class FlatDependentList<E> extends AbstractList<E> {
	protected E[][] incrementalDependents;
	protected int blockSize;

	public FlatDependentList(E[][] incrementalDependents) {
		this.incrementalDependents = incrementalDependents;
		if (incrementalDependents.length == 0)
			blockSize = 1;
		else
			blockSize = incrementalDependents[0].length;
		if (Stream.of(incrementalDependents).anyMatch(block -> block.length != blockSize))
			throw new RuntimeException("Blocks in incremental dependents have different lengths");
	}

	@Override
	public E get(int index) {
		return incrementalDependents[index / blockSize][index % blockSize];
	}

	@Override
	public int size() {
		return incrementalDependents.length;
	}
}
