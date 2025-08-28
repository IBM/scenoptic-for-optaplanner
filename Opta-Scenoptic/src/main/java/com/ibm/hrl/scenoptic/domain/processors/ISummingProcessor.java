package com.ibm.hrl.scenoptic.domain.processors;

public interface ISummingProcessor {
	ISummingProcessor nextProcessor(Object key);

	boolean isEmpty();

	// For debugging only!
	String valueString();
}

