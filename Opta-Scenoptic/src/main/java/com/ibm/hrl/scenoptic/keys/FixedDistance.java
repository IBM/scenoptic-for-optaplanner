package com.ibm.hrl.scenoptic.keys;

import java.util.stream.Stream;

public class FixedDistance implements ISpanKey {
    protected CellKey fixedCell;

    public FixedDistance(String sheet, int row, int col) {
        this.fixedCell = new CellKey(sheet, row, col);
    }

    public Stream<? extends  IKey> getElements(IKey key) {
        return fixedCell.getElements();
    }
}
