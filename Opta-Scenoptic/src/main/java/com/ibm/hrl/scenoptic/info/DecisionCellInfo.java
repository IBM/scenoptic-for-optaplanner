package com.ibm.hrl.scenoptic.info;

import com.ibm.hrl.scenoptic.domain.Cell;
import com.ibm.hrl.scenoptic.domain.PlanningCell;
import com.ibm.hrl.scenoptic.keys.AbstractCellKey;


import java.util.function.Function;
import java.util.stream.Stream;

public class DecisionCellInfo<K extends Comparable<K>> {
    protected K cells;
    protected Function<K, Cell<K, ?>> function;

    public DecisionCellInfo(K cells, Function<K, Cell<K, ?>> function) {
        this.cells = cells;
        this.function = function;
    }

    public Stream<PlanningCell<K, ?>> getValues() {
        return ((AbstractCellKey)cells).getElements().map(key -> (PlanningCell<K, ?>)function.apply((K)key));
    }
}
