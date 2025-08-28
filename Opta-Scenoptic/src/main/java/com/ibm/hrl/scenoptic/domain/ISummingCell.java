package com.ibm.hrl.scenoptic.domain;

import java.util.Collections;
import java.util.List;

public interface ISummingCell<K extends Comparable<K>, T>
		extends ICellWithIncrementalPredecessors<K>,
		ICellWithInitialization<K, T> {
	K getKey();

//	ISummingCell<K, T> getValue();

	void register(List<Object> registerKey, K dependent);

	void unregister(List<Object> registerKey, K dependent);

	void add(List<Object> registerKey, int value);

	void add(List<Object> registerKey, double value);

	void add(List<Object> registerKey, List<Object> dynamicKey, int value);

	void add(List<Object> registerKey, List<Object> dynamicKey, double value);

	default int getInt(List<Object> registerKey) {
		return getInt(registerKey, Collections.emptyList());
	}

	default double getDouble(List<Object> registerKey) {
		return getDouble(registerKey, Collections.emptyList());
	}

	int getInt(List<Object> registerKey, List<Object> dynamicKey);

	double getDouble(List<Object> registerKey, List<Object> dynamicKey);

	ISummingCell<?, T> setEnabled(boolean enabled);

	String details();
}
