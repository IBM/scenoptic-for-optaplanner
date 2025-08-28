package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.domain.descriptors.Formula;
import com.ibm.hrl.scenoptic.domain.descriptors.PredecessorFormula;

import java.util.List;

public abstract class DraggedCell<K extends Comparable<K>, T> extends ShadowCell<K, T> {
    protected PredecessorFormula<K> predecessorFormula;

    public DraggedCell() {
    }

    public DraggedCell(K key, List<K> predecessors, com.ibm.hrl.scenoptic.domain.descriptors.Formula formula, com.ibm.hrl.scenoptic.domain.descriptors.PredecessorFormula<K> predecessorFormula) {
        super(key, predecessors, formula);
        this.predecessorFormula = predecessorFormula;
    }

    public DraggedCell(K key, T value, List<K> predecessors, Formula formula, PredecessorFormula<K> predecessorFormula) {
        super(key, value, predecessors, formula);
        this.predecessorFormula = predecessorFormula;
    }
}
