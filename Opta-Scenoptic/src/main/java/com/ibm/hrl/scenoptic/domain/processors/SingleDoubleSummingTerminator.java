package com.ibm.hrl.scenoptic.domain.processors;

import java.util.List;

public class SingleDoubleSummingTerminator implements ISingleDoubleProcessor, ITerminatorProcessor {
	protected double value = 0;

	@Override
	public double getDouble(List<Object> dynamicKey) {
		assert dynamicKey == null || dynamicKey.isEmpty();
		return value;
	}

	@Override
	public void add(List<Object> dynamicKey, double value) {
		assert dynamicKey == null || dynamicKey.isEmpty();
		this.value += value;
	}

	@Override
	public boolean isEmpty() {
		return value == 0.0;
	}

	public String valueString() {
		return Double.toString(value);
	}
}
