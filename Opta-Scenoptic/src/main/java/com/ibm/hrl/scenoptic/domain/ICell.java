package com.ibm.hrl.scenoptic.domain;

public interface ICell<K, T> {
	K getKey();

	T getValue();

	void setValue(T newValue);
}