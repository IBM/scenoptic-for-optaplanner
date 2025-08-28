package com.ibm.hrl.test.sc.supplychain.bool.solver;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import com.ibm.hrl.scenoptic.keys.AbstractCellKey;
import com.ibm.hrl.scenoptic.keys.CellKey;
import com.ibm.hrl.scenoptic.solver.SpreadsheetScoreCalculator;

public class SCBoolSCScoreCalculator extends SpreadsheetScoreCalculator<AbstractCellKey, SCBoolSCSolution<AbstractCellKey>>
implements EasyScoreCalculator<SCBoolSCSolution<AbstractCellKey>, BendableScore> {
   	protected BendableScore zero = BendableScore.zero(1, 1);

	@Override
	public BendableScore calculateScore(SCBoolSCSolution<AbstractCellKey> solution) {
		try {
			return BendableScore.of(
					new int[] { -((int) getValue(solution, new CellKey("S>s bool self-contained V2", 23, 2))) },
					new int[] { -((int) getIncrementalValue(solution, new CellKey("S>s bool self-contained V2", 16, 5))) });
		} catch (MissingValue e) {
			return zero;
		}
	}
}
