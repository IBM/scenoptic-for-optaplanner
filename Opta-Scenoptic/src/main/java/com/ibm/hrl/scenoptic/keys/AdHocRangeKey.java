package com.ibm.hrl.scenoptic.keys;

public class AdHocRangeKey extends RangeKey {
	public AdHocRangeKey(String sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
		super(sheet, firstRow, lastRow, firstCol, lastCol);
	}

	static protected final boolean isAdHoc = true;

	public boolean isAdHoc() {
		return isAdHoc;
	}

	@Override
	public AdHocRangeKey down(int n) {
		return new AdHocRangeKey(sheet, firstRow + n, firstCol, lastRow + n, lastCol);
	}

	@Override
	public AdHocRangeKey right(int n) {
		return new AdHocRangeKey(sheet, firstRow, firstCol + n, lastRow, lastCol + n);
	}

	@Override
	public AbstractCellKey move(int down, int right) {
		return new AdHocRangeKey(sheet, firstRow + down, firstCol + right, lastRow + down, lastCol + right);
	}
}
