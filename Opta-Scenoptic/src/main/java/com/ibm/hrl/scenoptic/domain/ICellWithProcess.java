package com.ibm.hrl.scenoptic.domain;

public interface ICellWithProcess {
	default void startProcessing() {
	}

	default void endProcessing() {
	}
}
