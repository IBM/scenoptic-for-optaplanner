package com.ibm.hrl.scenoptic.domain.processors;

import java.util.List;

public class SingleIntSummingTerminator implements ISingleIntProcessor, ITerminatorProcessor {
	protected int value = 0;

	@Override
	public int getInt(List<Object> dynamicKey) {
		assert dynamicKey == null || dynamicKey.isEmpty();
		return value;
	}

	@Override
	public void add(List<Object> dynamicKey, int value) {
		assert dynamicKey == null || dynamicKey.isEmpty();
		this.value += value;
	}

	@Override
	public boolean isEmpty() {
		return value == 0;
	}

	@Override
	public String toString() {
		return "<SingleIntSummingTerminator " + value + ">";
	}

	public String valueString() {
		return Integer.toString(value);
	}
}
