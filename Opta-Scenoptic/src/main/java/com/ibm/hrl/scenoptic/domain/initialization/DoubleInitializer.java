package com.ibm.hrl.scenoptic.domain.initialization;

public class DoubleInitializer implements IncrementalInitializer<Double> {
    public static DoubleInitializer SINGLETON = new DoubleInitializer();

    @Override
    public Double addValue(Double acc, Double value) {
        if (acc == null)
            return value;
        if (value == null)
            return acc;
        return acc + value;
    }

    @Override
    public Double removeValue(Double acc, Double value) {
        if (value == null)
            return acc;
        if (acc == null)
            return -value;
        return acc - value;
    }
}
