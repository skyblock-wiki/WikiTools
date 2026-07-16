package org.hsw.wikitools.feature.copy_opened_ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ChestContainer {
    public static final int COLUMNS_AMOUNT = 9;

    private final List<List<Optional<InventorySlot>>> itemGrid;

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
            List<Optional<InventorySlot>> list = itemGrid.get(rowIndex);
            for (int colIndex = 0; colIndex < COLUMNS_AMOUNT; colIndex++) {
                list.add(Optional.empty());
            }
        }
    }

    public Optional<InventorySlot> getGridCell(int rowIndex, int colIndex) {
        return itemGrid.get(rowIndex).get(colIndex);
    }

    private void setGridCell(int rowIndex, int colIndex, Optional<InventorySlot> inventorySlot) {
        itemGrid.get(rowIndex).set(colIndex, inventorySlot);
    }

    public void populateGrid(Function<CellPosition, Optional<InventorySlot>> inventorySlotSupplier) {
        int i = 0;
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < COLUMNS_AMOUNT; colIndex++) {
                CellPosition cellPosition = new CellPosition(rowIndex, colIndex, i);
                Optional<InventorySlot> inventorySlot = inventorySlotSupplier.apply(cellPosition);
                setGridCell(rowIndex, colIndex, inventorySlot);
                i++;
            }
        }
    }

    public record CellPosition(int rowIndex, int colIndex, int i) { }
}
