package com.ibm.hrl.scenoptic.keys;

import java.util.*;
import java.util.stream.Collectors;

public class PredecessorCellKey {
    protected AbstractCellKey key;
    protected List<ISpanKey> spans;
    protected Map<AbstractCellKey, AbstractCellKey[]> predecessors;


    public PredecessorCellKey(AbstractCellKey key, List<ISpanKey> spans) {
        this.key = key;
        this.spans = spans;
        preCompute();
    }


    public Map<AbstractCellKey, AbstractCellKey[]> getPredecessorInfo() {
        return predecessors;
    }

    private void preCompute() {
        predecessors = key.getElements().map(cell -> Map.entry(cell, spans.stream()
                        .flatMap(dist -> dist.getElements(cell)).toArray(AbstractCellKey[]::new)))
                .collect(Collectors.toMap( entry -> entry.getKey(), entry -> entry.getValue()));
    }
}
