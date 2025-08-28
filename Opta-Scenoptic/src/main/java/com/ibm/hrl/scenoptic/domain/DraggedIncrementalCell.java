package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalFreshFormula;
import com.ibm.hrl.scenoptic.domain.initialization.IncrementalInitializer;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalPredecessorFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.IncrementalUpdateFormula;
import com.ibm.hrl.scenoptic.domain.descriptors.PredecessorFormula;
import org.optaplanner.core.api.domain.entity.PlanningEntity;

import java.util.List;

@PlanningEntity
public abstract class DraggedIncrementalCell<K extends Comparable<K>, T> extends IncrementalCell<K, T> {
    protected PredecessorFormula<K> predecessorFormula;

    // No-arg constructor required for OptaPlanner
    public DraggedIncrementalCell() {
    }

    public DraggedIncrementalCell(K key, List<K> predecessors, int numberOfPredecessorLists, IncrementalPredecessorFormula<K> incrementalPredecessors,
                                  IncrementalUpdateFormula incrementalUpdateFormula, IncrementalFreshFormula incrementalFreshFormula,
                                  PredecessorFormula<K> predecessorFormula, IncrementalInitializer<T> initializer) {
        super(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula, initializer);
        this.predecessorFormula = predecessorFormula;
    }

    public DraggedIncrementalCell(K key, T value, List<K> predecessors, int numberOfPredecessorLists, IncrementalPredecessorFormula<K> incrementalPredecessors,
                                  IncrementalUpdateFormula incrementalUpdateFormula, IncrementalFreshFormula incrementalFreshFormula,
                                  PredecessorFormula<K> predecessorFormula, IncrementalInitializer<T> initializer) {
        this(key, predecessors, numberOfPredecessorLists, incrementalPredecessors, incrementalUpdateFormula, incrementalFreshFormula,
                predecessorFormula, initializer);
        this.setValue(value);
    }

    // FIXME!!!! are all these necessary?
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void unsafeSetValue(Object value) {
        this.value = (T) value;
    }

    public int getNonNullInputs() {
        return nonNullInputs;
    }

    public void addNonNullInput(int delta) {
        nonNullInputs += delta;
    }

    public void resetNonNullInput() {
        nonNullInputs = 0;
    }

    public T getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Object initialValue) {
        if (this.initialValue != null)
            throw new RuntimeException("Attempt to modify initial value of incremental cell " + key);
        this.initialValue = (T) initialValue;
    }

    public void resetInitialValue(Object initialValue) {
        this.initialValue = (T) initialValue;
    }

    public T applyInitialValue() {
        value = initializer.addValue(value, initialValue);
        return value;
    }

    public T withdrawInitialValue() {
        value = initializer.removeValue(value, initialValue);
        return value;
    }

    public int numberOfIncrementalPredecessors() {
        return numberOfPredecessorLists;
    }

    public K[] getIncrementalPredecessors(K key, int i) {
        return incrementalPredecessors.apply(key, i);
    }

    public IncrementalUpdateFormula getIncrementalUpdateFormula() {
        return incrementalUpdateFormula;
    }

    public IncrementalFreshFormula getIncrementalFreshFormula() {
        return incrementalFreshFormula;
    }
}
