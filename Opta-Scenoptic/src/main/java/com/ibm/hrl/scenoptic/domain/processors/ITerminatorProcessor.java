package com.ibm.hrl.scenoptic.domain.processors;

public interface ITerminatorProcessor extends ISummingProcessor {
	@Override
	default ISummingProcessor nextProcessor(Object key) {
		throw new RuntimeException("Terminator processor " + this + " doesn't have a next processor");
	}
}
