package com.ibm.hrl.scenoptic.keys;

import java.util.stream.Stream;

public class CellDistance implements ISpanKey {
    protected CellKey key;

    protected String sheet;
    protected int rowDistance;
    protected int colDistance;

    public CellDistance(String sheet, int row, int col) {
        this.sheet = sheet;
        this.colDistance = col;
        this.rowDistance = row;
    }

    public Stream<? extends  IKey> getElements(IKey key) {
        return (new CellKey(sheet,
                ((CellKey)key).getRow() + rowDistance,
                ((CellKey)key).getCol() + colDistance)).getElements();
    }
}
