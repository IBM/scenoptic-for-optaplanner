package com.ibm.hrl.scenoptic.domain;

import java.util.List;

import com.ibm.hrl.scenoptic.domain.descriptors.Formula;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@PlanningEntity
public abstract class ShadowCell<K extends Comparable<K>, T> extends ComputedCell<K, T> {
    @PlanningId
    protected K key;
    protected T value;

    // No-arg constructor required for OptaPlanner
    public ShadowCell() {
        super(null, null);
    }

    public ShadowCell(K key, List<K> predecessors, Formula formula) {
        super(predecessors, formula);
        this.setKey(key);
    }

    public ShadowCell(K key, T value, List<K> predecessors, Formula formula) {
        super(predecessors, formula);
        this.setKey(key);
        this.setValue(value);
    }

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
}
