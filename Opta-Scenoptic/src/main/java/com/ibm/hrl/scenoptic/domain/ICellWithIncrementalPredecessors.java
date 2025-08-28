package com.ibm.hrl.scenoptic.domain;

public interface ICellWithIncrementalPredecessors<K extends Comparable<K>> {
	K getKey();

	int numberOfIncrementalPredecessors();

	K[] getIncrementalPredecessors(K key, int i);
}
