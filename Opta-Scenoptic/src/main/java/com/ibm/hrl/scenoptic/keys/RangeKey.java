package com.ibm.hrl.scenoptic.keys;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.director.ScoreDirector;

import com.ibm.hrl.scenoptic.domain.SpreadsheetProblem;

import java.util.Objects;
import java.util.stream.IntStream;

public class RangeKey extends AbstractCellKey {
    protected int firstRow;
    protected int lastRow;
    protected int firstCol;
    protected int lastCol;

    public RangeKey(String sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        super(sheet);
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.firstCol = firstCol;
        this.lastCol = lastCol;
    }

    @Override
    public Stream<AbstractCellKey> getElements() {
		return IntStream
				.range(firstRow, lastRow + 1)
				.mapToObj(row -> IntStream
						.range(firstCol, lastCol + 1)
						.mapToObj(col -> new CellKey(sheet, row, col)))
				.flatMap(stream -> stream);
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public int getFirstCol() {
        return firstCol;
    }

    public int getLastCol() {
        return lastCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheet, firstCol, firstRow, lastCol, lastRow);
    }

    @Override
    public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
        RangeKey other = (RangeKey) obj;
        return sheet.equals(other.sheet) && firstCol == other.firstCol && firstRow == other.firstRow && lastCol == other.lastCol && lastRow == other.lastRow;
    }

    @Override
    public int compareTo(AbstractCellKey o) {
		if (!(o instanceof RangeKey))
			return -1;
        RangeKey otherRK = (RangeKey) o;
        int compSheet = sheet.compareTo(o.sheet);
		if (compSheet != 0)
			return compSheet;
        int compFirstRow = Integer.compare(firstRow, otherRK.firstRow);
		if (compFirstRow != 0)
			return compFirstRow;
        int compLastRow = Integer.compare(lastRow, otherRK.lastRow);
		if (compLastRow != 0)
			return compLastRow;
        int compFirstCol = Integer.compare(firstCol, otherRK.firstCol);
		if (compFirstCol != 0)
			return compFirstCol;
        int compLastCol = Integer.compare(lastCol, otherRK.lastCol);
		if (compLastCol != 0)
			return compLastCol;
        int compAdHoc = Boolean.compare(isAdHoc(), otherRK.isAdHoc());
        return compAdHoc;
    }

    @Override
    public RangeKey down(int n) {
        return new RangeKey(sheet, firstRow + n, firstCol, lastRow + n, lastCol);
    }

    @Override
    public RangeKey right(int n) {
        return new RangeKey(sheet, firstRow, firstCol + n, lastRow, lastCol + n);
    }

    @Override
    public AbstractCellKey move(int down, int right) {
        return new RangeKey(sheet, firstRow + down, firstCol + right, lastRow + down, lastCol + right);
    }

    @Override
    public String toString() {
		return "<" + getClass().getSimpleName() + " " + sheet + "!" + cellDesignator(firstRow, firstCol) + ":"
				+ cellDesignator(lastRow, lastCol) + ">";
    }

    @SuppressWarnings({"rawtypes"})
    public String valuesAsString(SpreadsheetProblem solution, ScoreDirector scoreDirector) {
        String sep = "";
        StringBuilder result = new StringBuilder();
        for (int row = firstRow; row <= lastRow; row++) {
            result.append(sep);
            sep = "// ";
            for (int col = firstCol; col <= lastCol; col++) {
                result.append(solution.findCell(new CellKey(sheet, row, col), scoreDirector).getValue() + " ");
            }
        }
        return result.toString();
    }
}
