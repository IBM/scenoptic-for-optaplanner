package com.ibm.hrl.scenoptic.solver;

import com.ibm.hrl.scenoptic.domain.SpreadsheetProblem;

public class SpreadsheetScoreCalculator<K extends Comparable<K>, Solution extends SpreadsheetProblem<K>> {
	public static class MissingValue extends Exception {
		private static final long serialVersionUID = -8280555954243236189L;

		public MissingValue() {
		}
	}

	protected Object getValueGeneral(Solution solution, K cellName) throws MissingValue {
		Object result = solution.findCell(cellName, null).getValue();
		if (result == null)
			throw new MissingValue();
		return result;
	}

	protected Object getValue(Solution solution, K cellName) throws MissingValue {
		Object result = solution.getCell(cellName).getValue();
		if (result == null)
			throw new MissingValue();
		return result;
	}

	protected Object getIncrementalValue(Solution solution, K cellName) throws MissingValue {
		Object result = solution.getIncremental(cellName).getValue();
		if (result == null)
			throw new MissingValue();
		return result;
	}

}
