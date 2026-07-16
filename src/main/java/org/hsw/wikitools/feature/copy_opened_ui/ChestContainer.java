package org.hsw.wikitools.feature.copy_opened_ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ChestContainer {
    public static final int NUMCOLS = 9;

    private final List<List<Optional<Invslot>>> itemGrid;

    public final String containerName;
    public final int numRows;

    public ChestContainer(String containerName, int numRows) {
        this.containerName = containerName;
        this.numRows = numRows;

        this.itemGrid = new ArrayList<>();
        initializeItemGrid();
    }

    private void initializeItemGrid() {
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            itemGrid.add(new ArrayList<>());
            List<Optional<Invslot>> list = itemGrid.get(rowIndex);
            for (int colIndex = 0; colIndex < NUMCOLS; colIndex++) {
                list.add(Optional.empty());
            }
        }
    }

    public Optional<Invslot> getGridCell(int rowIndex, int colIndex) {
        return itemGrid.get(rowIndex).get(colIndex);
    }

    private void setGridCell(int rowIndex, int colIndex, Optional<Invslot> invslot) {
        itemGrid.get(rowIndex).set(colIndex, invslot);
    }

    public void populateGrid(Function<CellPosition, Optional<Invslot>> invslotSupplier) {
        int i = 0;
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < NUMCOLS; colIndex++) {
                Optional<Invslot> gridCell = getGridCell(rowIndex, colIndex);
                CellPosition cellPosition = new CellPosition(rowIndex, colIndex, i);
                Optional<Invslot> invslot = invslotSupplier.apply(cellPosition);
                setGridCell(rowIndex, colIndex, invslot);
                i++;
            }
        }
    }

    public static class CellPosition {
        public final int rowIndex;
        public final int colIndex;
        public final int i;

        public CellPosition(int rowIndex, int colIndex, int i) {
            this.rowIndex = rowIndex;
            this.colIndex = colIndex;
            this.i = i;
        }
    }
}
