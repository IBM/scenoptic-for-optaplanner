package com.ibm.hrl.scenoptic.domain.initialization;

public class IntegerInitializer implements IncrementalInitializer<Integer> {
    public static IntegerInitializer SINGLETON = new IntegerInitializer();

    @Override
    public Integer addValue(Integer acc, Integer value) {
        if (acc == null)
            return value;
        if (value == null)
            return acc;
        return acc + value;
    }

    @Override
    public Integer removeValue(Integer acc, Integer value) {
        if (value == null)
            return acc;
        if (acc == null)
            return -value;
        return acc - value;
    }
}
