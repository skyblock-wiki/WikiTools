package org.hsw.wikitools.feature.copy_opened_ui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class CompleteUiGrid {
    public final int numRows;
    public final int numCols;

    public final List<List<Optional<UiTemplateItem>>> itemGrid;

    public final Optional<UiTemplateItem> uniqueCloseItem;
    public final Optional<UiTemplateItem> uniqueGoBackItem;

    public CompleteUiGrid(int numRows, int numCols, List<List<Optional<UiTemplateItem>>> itemGrid)
            throws InvalidChestContentException {
        this.numRows = numRows;
        this.numCols = numCols;

        this.itemGrid = itemGrid;

        validateItemGrid();

        this.uniqueCloseItem = popUniqueCloseItemIfExists();
        this.uniqueGoBackItem = popUniqueGoBackItemIfExists();
    }

    private void validateItemGrid() throws InvalidChestContentException {
        boolean numRowsIsValid = itemGrid.size() == numRows;
        if (!numRowsIsValid) {
            throw new InvalidChestContentException(
                    "ChestContainer instantiated with numRows=" + numRows +
                            " but has an actual height of " + itemGrid.size());
        }

        int[] rowSizes = itemGrid.stream().mapToInt(List::size).toArray();
        boolean numColsIsValid = Arrays.stream(rowSizes).allMatch(rowSize -> rowSize == numCols);
        if (!numColsIsValid) {
            String rowSizesJoined = String.join(",", Arrays.stream(rowSizes).mapToObj(String::valueOf).toArray(String[]::new));
            throw new InvalidChestContentException(
                    "ChestContainer instantiated with numCols=" + numCols +
                            "but has actual widths of [" + rowSizesJoined + "]"
            );
        }
    }

    private Optional<UiTemplateItem> getGridCell(int rowIndex, int colIndex) {
        return itemGrid.get(rowIndex).get(colIndex);
    }

    private void clearGridCell(int rowIndex, int colIndex) {
        itemGrid.get(rowIndex).set(colIndex, Optional.empty());
    }

    private Optional<UiTemplateItem> popUniqueCloseItemIfExists() {
        int numberOfValidCloses = 0;
        Optional<UiTemplateItem> uniqueValidClose = Optional.empty();

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < numCols; colIndex++) {
                Optional<UiTemplateItem> gridCell = getGridCell(rowIndex, colIndex);
                if (gridCell.isPresent() && gridCell.get().isValidClose()) {
                    numberOfValidCloses++;
                    uniqueValidClose = numberOfValidCloses < 2 ? gridCell : Optional.empty();
                }
            }
        }

        uniqueValidClose.ifPresent(uiTemplateItem -> clearGridCell(uiTemplateItem.rowIndex, uiTemplateItem.colIndex));
        return uniqueValidClose;
    }

    private Optional<UiTemplateItem> popUniqueGoBackItemIfExists() {
        int numberOfValidGoBacks = 0;
        Optional<UiTemplateItem> uniqueValidGoBack = Optional.empty();

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < numCols; colIndex++) {
                Optional<UiTemplateItem> gridCell = getGridCell(rowIndex, colIndex);
                if (gridCell.isPresent() && gridCell.get().isValidGoBack()) {
                    numberOfValidGoBacks++;
                    uniqueValidGoBack = numberOfValidGoBacks < 2 ? gridCell : Optional.empty();
                }
            }
        }

        uniqueValidGoBack.ifPresent(uiTemplateItem -> clearGridCell(uiTemplateItem.rowIndex, uiTemplateItem.colIndex));
        return uniqueValidGoBack;
    }
}
