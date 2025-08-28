package com.ibm.hrl.scenoptic.domain;

import java.util.List;

public interface IUpdatingCell<K, T> extends ICell<K, T> {
	void changing(List<Object> previousValues, List<Object> newValues);
}
