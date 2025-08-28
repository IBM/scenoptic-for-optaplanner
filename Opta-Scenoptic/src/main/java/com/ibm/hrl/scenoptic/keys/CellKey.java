package com.ibm.hrl.scenoptic.keys;

import java.util.Objects;
import java.util.stream.Stream;

public class CellKey extends AbstractCellKey {
	protected int row;
	protected int col;

	public CellKey(String sheet, int row, int col) {
		super(sheet);
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	@Override
	public Stream<AbstractCellKey> getElements() {
		return Stream.of(this);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sheet, col, row);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CellKey other = (CellKey) obj;
		return sheet.equals(other.sheet) && col == other.col && row == other.row;
	}

	@Override
	public int compareTo(AbstractCellKey o) {
		if (!(o instanceof CellKey))
			return -1;
		CellKey otherCK = (CellKey) o;
		int compSheet = sheet.compareTo(o.sheet);
		if (compSheet != 0)
			return compSheet;
		int compRow = Integer.compare(row, otherCK.row);
		if (compRow != 0)
			return compRow;
		int compCol = Integer.compare(col, otherCK.col);
		return compCol;
	}

	@Override
	public CellKey down(int n) {
		return new CellKey(sheet, row + n, col);
	}

	@Override
	public CellKey right(int n) {
		return new CellKey(sheet, row, col + n);
	}

	@Override
	public AbstractCellKey move(int down, int right) {
		return new CellKey(sheet, row + down, col + right);
	}

	@Override
	public String toString() {
		return "<" + getClass().getSimpleName() + " " + sheet + "!" + cellDesignator(row, col) + ">";
	}
	
	public String coords() {
		return cellDesignator(row, col);
	}
}
