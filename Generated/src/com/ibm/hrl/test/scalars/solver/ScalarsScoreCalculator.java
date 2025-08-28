package com.ibm.hrl.test.scalars.solver;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.solver.SpreadsheetScoreCalculator;

public class ScalarsScoreCalculator extends SpreadsheetScoreCalculator<AbstractCellKey, ScalarsSolution<AbstractCellKey>>
implements EasyScoreCalculator<ScalarsSolution<AbstractCellKey>, BendableScore> {
   	protected BendableScore zero = BendableScore.zero(0, 1);

	@Override
	public BendableScore calculateScore(ScalarsSolution<AbstractCellKey> solution) {
		try {
			return BendableScore.of(
					new int[] { },
					new int[] { (int) getIncrementalValue(solution, new CellKey("Scalars", 1, 4)) });
		} catch (MissingValue e) {
			return zero;
		}
	}
}
