package com.ibm.hrl.scenoptic.domain.descriptors;

public interface IncrementalPredecessorFormula<K> {
    K[] apply(K current, int index);
}
