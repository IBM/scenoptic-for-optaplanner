package com.ibm.hrl.test.sc.supplychain.nonneg.solver;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.solver.SpreadsheetScoreCalculator;

public class SCNonnegSCScoreCalculator extends SpreadsheetScoreCalculator<AbstractCellKey, SCNonnegSCSolution<AbstractCellKey>>
implements EasyScoreCalculator<SCNonnegSCSolution<AbstractCellKey>, BendableScore> {
   	protected BendableScore zero = BendableScore.zero(1, 1);

	@Override
	public BendableScore calculateScore(SCNonnegSCSolution<AbstractCellKey> solution) {
		try {
			return BendableScore.of(
					new int[] { (int) getIncrementalValue(solution, new CellKey("objectives!$J$3:J15", 1, 19)) },
					new int[] { -((int) getIncrementalValue(solution, new CellKey("Non-neg self-contained V2", 16, 5))) });
		} catch (MissingValue e) {
			return zero;
		}
	}
}
