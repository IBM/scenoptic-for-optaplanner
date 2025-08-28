package com.ibm.hrl.scenoptic.domain.descriptors;

import com.ibm.hrl.scenoptic.utils.DefaultArrayList;

public interface Formula {
	Object apply(DefaultArrayList<Object> values);
}
