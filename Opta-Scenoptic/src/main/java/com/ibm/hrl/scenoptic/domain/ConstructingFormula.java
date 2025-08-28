package com.ibm.hrl.scenoptic.domain;

public interface ConstructingFormula<K, T> {
	T create(K key);
}
