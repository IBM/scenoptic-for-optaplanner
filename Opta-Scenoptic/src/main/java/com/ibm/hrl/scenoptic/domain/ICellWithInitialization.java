package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.utils.DefaultArrayList;

import java.util.stream.Stream;

public interface ICellWithInitialization<K extends Comparable<K>, T>
		extends ICell<K, T>, ICellWithIncrementalPredecessors<K> {
	T getInitialValue();

	void setInitialValue(Object initialValue);

	void addNonNullInput(int delta);

	Stream<K> getPredecessors();

	Object applyIncrementalFreshFormula(Object subjectValue, DefaultArrayList<Object> newValues, DefaultArrayList<Object> newScalars);

	T applyInitialValue();
}
