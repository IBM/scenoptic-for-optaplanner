package com.ibm.hrl.scenoptic.domain.processors;

import java.util.List;

public interface IMultipleValueProcessor extends ISummingProcessor {
	int getInt(List<Object> dynamicKey, int index);

	double getDouble(List<Object> dynamicKey, int index);

	void add(List<Object> dynamicKey, int value, int index);

	void add(List<Object> dynamicKey, double value, int index);
}
