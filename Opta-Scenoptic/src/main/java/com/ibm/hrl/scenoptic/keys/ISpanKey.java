package com.ibm.hrl.scenoptic.keys;

import java.util.stream.Stream;

public interface ISpanKey {
    Stream<? extends IKey> getElements(IKey key);
}
