package com.ibm.hrl.scenoptic.info;

import com.ibm.hrl.scenoptic.domain.descriptors.Formula;
import com.ibm.hrl.scenoptic.keys.AbstractCellKey;

import java.util.stream.Stream;

public class ShadowCellInfo<K extends Comparable<K>> {
	protected K cells;
	protected Formula function;

	public ShadowCellInfo(K cells, Formula formula) {
		this.cells = cells;
		this.function = formula;
	}

	public Stream<ShadowCellInfo<K>> getElements() {
		return ((AbstractCellKey) cells).getElements().
				map(key -> new ShadowCellInfo<>((K) key, this.function));
	}

	public K getKey() {
		return this.cells;
	}

	public Formula getFormula() {
		return this.function;
	}
}
