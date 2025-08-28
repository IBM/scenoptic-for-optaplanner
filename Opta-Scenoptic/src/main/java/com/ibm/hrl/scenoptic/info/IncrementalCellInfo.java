package com.ibm.hrl.scenoptic.info;

import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalFreshFormula;
import com.ibm.hrl.scenoptic.domain.initialization.IncrementalInitializer;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalUpdateFormula;
import com.ibm.hrl.scenoptic.keys.AbstractCellKey;

import java.util.stream.Stream;

public class IncrementalCellInfo<K extends Comparable<K>> {
	protected K cells;
	protected int numberOfPredecessorLists;
	protected IncrementalPredecessorFormula<K> incrementalPredecessorsFormula;
	protected IncrementalUpdateFormula incrementalUpdateFormula;
	protected IncrementalFreshFormula incrementalFreshFormula;
	protected IncrementalInitializer<?> initializer;

	public IncrementalCellInfo(K cells,
	                           int numberOfPredecessorLists,
	                           IncrementalPredecessorFormula<K> incrementalPredecessorsFormula,
	                           IncrementalUpdateFormula incrementalUpdateFormula,
	                           IncrementalFreshFormula incrementalFreshFormula,
	                           IncrementalInitializer<?> initializer) {
		this.cells = cells;
		this.numberOfPredecessorLists = numberOfPredecessorLists;
		this.incrementalPredecessorsFormula = incrementalPredecessorsFormula;
		this.incrementalUpdateFormula = incrementalUpdateFormula;
		this.incrementalFreshFormula = incrementalFreshFormula;
		this.initializer = initializer;
	}

	public Stream<IncrementalCellInfo<K>> getElements() {
		return ((AbstractCellKey) cells).getElements().
				map(key -> new IncrementalCellInfo<>((K) key,
						this.numberOfPredecessorLists,
						this.incrementalPredecessorsFormula,
						this.incrementalUpdateFormula,
						this.incrementalFreshFormula,
						this.initializer));
	}

	public K getKey() {
		return this.cells;
	}

	public int getNumberOfPredecessorLists() {
		return numberOfPredecessorLists;
	}

	public IncrementalPredecessorFormula<K> getIncrementalPredecessorsFormula() {
		return incrementalPredecessorsFormula;
	}

	public IncrementalUpdateFormula getIncrementalUpdateFormula() {
		return incrementalUpdateFormula;
	}

	public IncrementalFreshFormula getIncrementalFreshFormula() {
		return incrementalFreshFormula;
	}

	public IncrementalInitializer<?> getInitializer() {
		return initializer;
	}
}
