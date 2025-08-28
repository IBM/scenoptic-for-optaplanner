package com.ibm.hrl.scenoptic.domain;

import com.ibm.hrl.scenoptic.keys.InitializationTriggerKey;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class InitializationTriggerCell extends PlanningCell<InitializationTriggerKey, Integer> {
    // No-arg constructor required for OptaPlanner
    public InitializationTriggerCell() {
    }

    public InitializationTriggerCell(InitializationTriggerKey key) {
        super(key);
    }

    @Override
    @PlanningVariable(valueRangeProviderRefs = "*Singleton-provider*")
    public Integer getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(Integer newValue) {
        super.setValue(newValue);
    }
}
