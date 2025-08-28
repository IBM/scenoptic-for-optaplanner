package com.ibm.hrl.test.complex.solver;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.solver.SpreadsheetScoreCalculator;

public class ComplexScoreCalculator extends SpreadsheetScoreCalculator<AbstractCellKey, ComplexSolution<AbstractCellKey>>
implements EasyScoreCalculator<ComplexSolution<AbstractCellKey>, BendableScore> {
   	protected BendableScore zero = BendableScore.zero(0, 1);

	@Override
	public BendableScore calculateScore(ComplexSolution<AbstractCellKey> solution) {
		try {
			return BendableScore.of(
					new int[] { },
					new int[] { (int) getValue(solution, new CellKey("Complex-expressions", 1, 5)) });
		} catch (MissingValue e) {
			return zero;
		}
	}
}
