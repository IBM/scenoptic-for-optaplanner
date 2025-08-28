package com.ibm.hrl.test.counting.solver;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.solver.SpreadsheetScoreCalculator;

public class CountingScoreCalculator extends SpreadsheetScoreCalculator<AbstractCellKey, CountingSolution<AbstractCellKey>>
implements EasyScoreCalculator<CountingSolution<AbstractCellKey>, BendableScore> {
   	protected BendableScore zero = BendableScore.zero(0, 1);

	@Override
	public BendableScore calculateScore(CountingSolution<AbstractCellKey> solution) {
		try {
			return BendableScore.of(
					new int[] { },
					new int[] { (int) SpreadsheetScoreCalculator.getIncrementalValue(solution, new CellKey("objectives!F1:G10", 1, 19)) });
		} catch (MissingValue e) {
			return zero;
		}
	}
}
