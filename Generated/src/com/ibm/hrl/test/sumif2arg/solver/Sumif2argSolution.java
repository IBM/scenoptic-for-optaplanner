package com.ibm.hrl.test.sumif2arg.solver;

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
public class Sumif2argSolution<K extends Comparable<K>> extends SpreadsheetProblem<K> {
	@PlanningScore(bendableHardLevelsSize = 1, bendableSoftLevelsSize = 0)
	private BendableScore score;

	// No-arg constructor required for OptaPlanner
	public Sumif2argSolution() {
	}

	public Sumif2argSolution(List<? extends PlanningCell<K, Integer>> decisions,
	        List<? extends ShadowCell<K, ?>> cells,
			List<? extends InputCell<K, ?>> inputs,
			List<? extends IncrementalCell<K, ?>> incrementals,
			List<? extends ISummingCell<K, ?>> summingCells) {
		super(decisions, cells, inputs, incrementals, summingCells);
	}

	@ValueRangeProvider(id = "Int0To2048")
	public ValueRange<Integer> getInt0To2048Range() {
		return ValueRangeFactory.createIntValueRange(0, 2048);
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
