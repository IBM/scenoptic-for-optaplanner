package com.ibm.hrl.test.sumif2arg.solver;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.solver.SpreadsheetScoreCalculator;

public class Sumif2argScoreCalculator extends SpreadsheetScoreCalculator<AbstractCellKey, Sumif2argSolution<AbstractCellKey>>
implements EasyScoreCalculator<Sumif2argSolution<AbstractCellKey>, BendableScore> {
   	protected BendableScore zero = BendableScore.zero(1, 0);

	@Override
	public BendableScore calculateScore(Sumif2argSolution<AbstractCellKey> solution) {
		try {
			return BendableScore.of(
					new int[] { (int) getIncrementalValue(solution, new CellKey("objectives!E1:F1", 1, 19)) },
					new int[] { });
		} catch (MissingValue e) {
			return zero;
		}
	}
}
