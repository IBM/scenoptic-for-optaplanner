package com.ibm.hrl.test.simplified.solver;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.solver.SpreadsheetScoreCalculator;

public class SimplifiedScoreCalculator extends SpreadsheetScoreCalculator<AbstractCellKey, SimplifiedSolution<AbstractCellKey>>
implements EasyScoreCalculator<SimplifiedSolution<AbstractCellKey>, BendableScore> {
   	protected BendableScore zero = BendableScore.zero(0, 1);

	@Override
	public BendableScore calculateScore(SimplifiedSolution<AbstractCellKey> solution) {
		try {
			return BendableScore.of(
					new int[] { },
					new int[] { (int) getValue(solution, new CellKey("Simplified", 5, 5)) });
		} catch (MissingValue e) {
			return zero;
		}
	}
}
