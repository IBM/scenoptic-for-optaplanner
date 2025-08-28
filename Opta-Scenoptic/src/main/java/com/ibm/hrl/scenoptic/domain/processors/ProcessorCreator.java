package com.ibm.hrl.scenoptic.domain.processors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ProcessorCreator<P extends ISummingProcessor> {
	P create(List<ProcessorCreator<P>> furtherCreators)
			throws InstantiationException, IllegalAccessException, InvocationTargetException;
}
