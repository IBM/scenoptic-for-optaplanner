package com.ibm.hrl.scenoptic.domain.processors;

import java.util.List;

import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;

public class SingleIntDisequalityProcessor implements ISingleIntProcessor, ITerminatorProcessor {
	protected int total = 0;
	protected int nonZeros = 0;
	protected ObjectIntHashMap<Object> values = ObjectIntHashMap.newMap();

	@Override
	public int getInt(List<Object> dynamicKey) {
		assert dynamicKey.size() == 1;
		return total - values.getIfAbsent(dynamicKey.get(0), 0);
	}

	@Override
	public void add(List<Object> dynamicKey, int value) {
		assert dynamicKey.size() == 1;
		final Object key = dynamicKey.get(0);
		int oldValue = values.getIfAbsent(key, 0);
		total += value - oldValue;
		if (value != oldValue) {
			if (value == 0)
				nonZeros--;
			else if (oldValue == 0)
				nonZeros++;
		}
		values.put(key, value);
	}

	@Override
	public boolean isEmpty() {
		return nonZeros == 0;
	}

	@Override
	public String valueString() {
		return "SingleIntDisequalityProcessor";
	}
}
