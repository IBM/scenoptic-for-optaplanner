package com.ibm.hrl.scenoptic.keys;

import java.util.stream.Stream;

public interface IKey {
    Stream<? extends IKey> getElements();
}
