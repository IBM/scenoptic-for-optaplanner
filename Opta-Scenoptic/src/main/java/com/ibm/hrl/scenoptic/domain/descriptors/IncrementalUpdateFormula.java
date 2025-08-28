package com.ibm.hrl.scenoptic.domain.descriptors;

import com.ibm.hrl.scenoptic.utils.DefaultArrayList;

public interface IncrementalUpdateFormula {
	Object apply(Object subjectValue, DefaultArrayList<Object> previousValues, DefaultArrayList<Object> newValues,
				 DefaultArrayList<Object> previousScalars, DefaultArrayList<Object> newScalars);
}
