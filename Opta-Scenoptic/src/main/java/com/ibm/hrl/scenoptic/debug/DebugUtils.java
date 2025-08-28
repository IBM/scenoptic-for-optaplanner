package com.ibm.hrl.scenoptic.debug;

public class DebugUtils {
    public static String objectId(Object o) {
        return String.format("%08X", System.identityHashCode(o));
    }
}

