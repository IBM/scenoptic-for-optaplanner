package com.ibm.hrl.scenoptic.domain.initialization;

public interface IncrementalInitializer<T> {
    T addValue(T acc, T value);

    T removeValue(T acc, T value);
}
