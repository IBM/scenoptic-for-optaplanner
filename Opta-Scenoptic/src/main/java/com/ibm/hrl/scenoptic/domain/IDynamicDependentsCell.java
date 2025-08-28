package com.ibm.hrl.scenoptic.domain;

import java.util.stream.Stream;

public interface IDynamicDependentsCell<K extends Comparable<K>> {
	Stream<K> getDynamicDependents();
}
