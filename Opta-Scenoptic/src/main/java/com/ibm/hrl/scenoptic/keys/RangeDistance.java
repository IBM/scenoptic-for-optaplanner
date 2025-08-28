package com.ibm.hrl.scenoptic.keys;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RangeDistance extends CellDistance implements ISpanKey{
    protected int lastRowDistance;
    protected int lastColDistance;

    public RangeDistance(String sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        super(sheet, firstRow, firstCol);
        this.lastRowDistance = lastRow;
        this.lastColDistance = lastCol;
    }

    public Stream<? extends  IKey> getElements(IKey key) {
        return IntStream
                .range(((CellKey)key).getRow() + rowDistance, ((CellKey)key).getRow() + lastRowDistance + 1)
                .mapToObj(row -> IntStream
                        .range(((CellKey)key).getCol() + colDistance, ((CellKey)key).getCol() + lastColDistance + 1)
                        .mapToObj(col -> new CellKey(sheet, row, col)))
                .flatMap(stream -> stream);
    }
}
