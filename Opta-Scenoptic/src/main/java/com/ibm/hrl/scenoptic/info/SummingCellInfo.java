package com.ibm.hrl.scenoptic.info;

import com.ibm.hrl.scenoptic.domain.ConstructingFormula;
import com.ibm.hrl.scenoptic.domain.ISummingCell;
import com.ibm.hrl.scenoptic.keys.AbstractCellKey;

import java.util.stream.Stream;

public class SummingCellInfo<K extends Comparable<K>> {
	protected K cells;
	protected ConstructingFormula<K, ? extends ISummingCell<K, ?>> creator;

	public SummingCellInfo(K cells, ConstructingFormula<K, ? extends ISummingCell<K, ?>> creator) {
		this.cells = cells;
		this.creator = creator;
	}

	public Stream<SummingCellInfo<K>> getElements() {
		return ((AbstractCellKey) cells).getElements().
				map(key -> new SummingCellInfo<>((K) key, this.creator));
	}

	public K getKey() {
		return this.cells;
	}

	public ConstructingFormula<K, ? extends ISummingCell<K, ?>> getCreator() {
		return this.creator;
	}
}
