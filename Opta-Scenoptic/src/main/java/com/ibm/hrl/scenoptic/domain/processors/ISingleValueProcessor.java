package com.ibm.hrl.scenoptic.domain.processors;

import java.util.List;

public interface ISingleValueProcessor extends ISummingProcessor {
	int getInt(List<Object> dynamicKey);

	double getDouble(List<Object> dynamicKey);

	void add(List<Object> dynamicKey, int value);

	void add(List<Object> dynamicKey, double value);
}
