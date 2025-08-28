package com.ibm.hrl.test.counting.solver;

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
public class CountingSolution<K extends Comparable<K>> extends SpreadsheetProblem<K> {
	@PlanningScore(bendableHardLevelsSize = 0, bendableSoftLevelsSize = 1)
	private BendableScore score;

	// No-arg constructor required for OptaPlanner
	public CountingSolution() {
	}

	public CountingSolution(List<? extends PlanningCell<K, Integer>> decisions,
	        List<? extends ShadowCell<K, ?>> cells,
			List<? extends InputCell<K, ?>> inputs,
			List<? extends IncrementalCell<K, ?>> incrementals,
			List<? extends ISummingCell<K, ?>> summingCells) {
		super(decisions, cells, inputs, incrementals, summingCells);
	}

	@ValueRangeProvider(id = "Int0To11")
	public ValueRange<int:Integer> getInt0To11Range() {
		return ValueRangeFactory.createIntValueRange(0, 11);
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
