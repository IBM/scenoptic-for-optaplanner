package com.ibm.hrl.test.sc.supplychain.countifs.solver;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;

import com.ibm.hrl.scenoptic.domain.IncrementalCell;
import com.ibm.hrl.scenoptic.domain.InputCell;
import com.ibm.hrl.scenoptic.domain.PlanningCell;
import com.ibm.hrl.scenoptic.domain.ShadowCell;
import com.ibm.hrl.scenoptic.domain.SpreadsheetProblem;
import com.ibm.hrl.scenoptic.domain.ISummingCell;

@PlanningSolution
public class SCCountifsSCSolution<K extends Comparable<K>> extends SpreadsheetProblem<K> {
	@PlanningScore(bendableHardLevelsSize = 1, bendableSoftLevelsSize = 1)
	private BendableScore score;

	// No-arg constructor required for OptaPlanner
	public SCCountifsSCSolution() {
	}

	public SCCountifsSCSolution(List<? extends PlanningCell<K, Integer>> decisions,
	        List<? extends ShadowCell<K, ?>> cells,
			List<? extends InputCell<K, ?>> inputs,
			List<? extends IncrementalCell<K, ?>> incrementals,
			List<? extends ISummingCell<K, ?>> summingCells) {
		super(decisions, cells, inputs, incrementals, summingCells);
	}

	@ValueRangeProvider(id = "Int1To1500")
	public ValueRange<Integer> getInt1To1500Range() {
		return ValueRangeFactory.createIntValueRange(1, 1500);
	}

	@ValueRangeProvider(id = "Int1To2000")
	public ValueRange<Integer> getInt1To2000Range() {
		return ValueRangeFactory.createIntValueRange(1, 2000);
	}

    @Override
	public BendableScore getScore() {
		return score;
	}

    @Override
	public void setScore(Score score) {
		this.score = (BendableScore) score;
	}
}
