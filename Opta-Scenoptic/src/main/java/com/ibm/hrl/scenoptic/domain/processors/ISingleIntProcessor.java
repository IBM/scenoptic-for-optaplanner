package com.ibm.hrl.scenoptic.domain.processors;

import java.util.List;

public interface ISingleIntProcessor extends ISingleValueProcessor {
	@Override
	default double getDouble(List<Object> dynamicKey) {
		throw new RuntimeException("An int processor doesn't support double values");
	}

	@Override
	default void add(List<Object> dynamicKey, double value) {
		throw new RuntimeException("An int processor doesn't support double values");
	}
}
