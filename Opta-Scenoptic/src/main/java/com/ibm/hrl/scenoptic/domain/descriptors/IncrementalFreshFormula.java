package com.ibm.hrl.scenoptic.domain.descriptors;

import com.ibm.hrl.scenoptic.utils.DefaultArrayList;

public interface IncrementalFreshFormula {
	Object apply(Object subjectValue, DefaultArrayList<Object> newValues, DefaultArrayList<Object> newScalars);
}
