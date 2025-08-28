package com.ibm.hrl.test.sc.supplychain.sumifs.solver;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.solver.SpreadsheetScoreCalculator;

public class SCSumifsSCScoreCalculator extends SpreadsheetScoreCalculator<AbstractCellKey, SCSumifsSCSolution<AbstractCellKey>>
implements EasyScoreCalculator<SCSumifsSCSolution<AbstractCellKey>, BendableScore> {
   	protected BendableScore zero = BendableScore.zero(1, 1);

	@Override
	public BendableScore calculateScore(SCSumifsSCSolution<AbstractCellKey> solution) {
		try {
			return BendableScore.of(
					new int[] { (int) getIncrementalValue(solution, new CellKey("Non-neg SUMIFS V2", 16, 10)) },
					new int[] { -((int) getIncrementalValue(solution, new CellKey("Non-neg SUMIFS V2", 16, 5))) });
		} catch (MissingValue e) {
			return zero;
		}
	}
}
