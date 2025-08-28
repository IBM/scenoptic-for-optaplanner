package com.ibm.hrl.scenoptic.domain.descriptors;

public interface PredecessorFormula<K> {
    K[] apply(K current);
}
