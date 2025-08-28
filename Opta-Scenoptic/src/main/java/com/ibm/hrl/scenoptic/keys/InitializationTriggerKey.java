package com.ibm.hrl.scenoptic.keys;

import java.util.stream.Stream;

public class InitializationTriggerKey implements IKey, Comparable<InitializationTriggerKey> {
    public static InitializationTriggerKey SINGLETON = new InitializationTriggerKey();

    public InitializationTriggerKey() {
    }

    @Override
    public Stream<? extends IKey> getElements() {
        return Stream.of(this);
    }

    @Override
    public int compareTo(InitializationTriggerKey o) {
        return o != null ? 0 : -1;
    }

    @Override
    public String toString() {
        return "InitializationTriggerKey";
    }
}
