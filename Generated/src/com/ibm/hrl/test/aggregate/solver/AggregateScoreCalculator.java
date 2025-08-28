package com.ibm.hrl.test.aggregate.solver;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.solver.SpreadsheetScoreCalculator;

public class AggregateScoreCalculator extends SpreadsheetScoreCalculator<AbstractCellKey, AggregateSolution<AbstractCellKey>>
implements EasyScoreCalculator<AggregateSolution<AbstractCellKey>, BendableScore> {
   	protected BendableScore zero = BendableScore.zero(0, 4);

	@Override
	public BendableScore calculateScore(AggregateSolution<AbstractCellKey> solution) {
		try {
			return BendableScore.of(
					new int[] { },
					new int[] { (int) getIncrementalValue(solution, new CellKey("Sheet1", 1, 5)),
					            (int) getIncrementalValue(solution, new CellKey("Sheet1", 2, 5)),
					            (int) getIncrementalValue(solution, new CellKey("Sheet1", 1, 4)),
					            (int) getIncrementalValue(solution, new CellKey("Sheet1", 2, 4)) });
		} catch (MissingValue e) {
			return zero;
		}
	}
}
