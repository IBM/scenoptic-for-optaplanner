package com.ibm.hrl.scenoptic.domain.processors;

import java.util.List;

public interface ISingleDoubleProcessor extends ISingleValueProcessor {
	@Override
	default int getInt(List<Object> dynamicKey) {
		throw new RuntimeException("An int processor doesn't support int values");
	}

	@Override
	default void add(List<Object> dynamicKey, int value) {
		throw new RuntimeException("An int processor doesn't support int values");
	}
}
