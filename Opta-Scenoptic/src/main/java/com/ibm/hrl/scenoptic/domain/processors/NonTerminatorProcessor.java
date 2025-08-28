package com.ibm.hrl.scenoptic.domain.processors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public abstract class NonTerminatorProcessor implements ISummingProcessor {
	protected List<ProcessorCreator> processorCreators;
	protected Map<Object, ISummingProcessor> nextProcessor;

	public NonTerminatorProcessor(List<ProcessorCreator> processorCreators) {
		this.processorCreators = processorCreators;
	}

	@Override
	public ISummingProcessor nextProcessor(Object key) {
		return nextProcessor.computeIfAbsent(key, k -> {
			try {
				return processorCreators.get(0).create(processorCreators.subList(1, processorCreators.size()));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
