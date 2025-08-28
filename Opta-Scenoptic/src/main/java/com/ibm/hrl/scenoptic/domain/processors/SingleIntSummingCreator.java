package com.ibm.hrl.scenoptic.domain.processors;

import java.util.List;

public class SingleIntSummingCreator implements ProcessorCreator<SingleIntSummingTerminator> {
	@Override
	public SingleIntSummingTerminator create(List<ProcessorCreator<SingleIntSummingTerminator>> furtherCreators) {
		return new SingleIntSummingTerminator();
	}
}
