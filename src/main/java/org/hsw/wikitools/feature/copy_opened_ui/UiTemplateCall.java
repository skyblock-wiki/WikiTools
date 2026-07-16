package org.hsw.wikitools.feature.copy_opened_ui;

import org.hsw.wikitools.common.MctextTemplateFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

class UiTemplateCall {
    private static final MctextTemplateFormatter UI_NAME_FORMATTER = new MctextTemplateFormatter(new HashMap<>());

    private final String uiName;
    private final CompleteUiGrid completeUiGrid;
    private final boolean fillWithBlankByDefault;

    private UiTemplateCall(
            String uiName,
            CompleteUiGrid completeUiGrid,
            boolean fillWithBlankByDefault
    ) {
        this.uiName = uiName;
        this.completeUiGrid = completeUiGrid;
        this.fillWithBlankByDefault = fillWithBlankByDefault;
    }

    public static UiTemplateCall of(
            ChestContainer chestContainer,
            boolean fillWithBlankByDefault,
            boolean alwaysUseMcItemNameForNonSkullItems
    ) throws InvalidChestContentException {
        List<List<Optional<UiTemplateItem>>> itemGrid = getItemGridFromChestContainer(
                chestContainer, fillWithBlankByDefault, alwaysUseMcItemNameForNonSkullItems);

        CompleteUiGrid completeUiGrid = new CompleteUiGrid(chestContainer.numRows, ChestContainer.COLUMNS_AMOUNT, itemGrid);

        return new UiTemplateCall(
                chestContainer.containerName,
                completeUiGrid,
                fillWithBlankByDefault
        );
    }

    private static List<List<Optional<UiTemplateItem>>> getItemGridFromChestContainer(
            ChestContainer chestContainer,
            boolean fillWithBlankByDefault,
            boolean alwaysUseMcItemNameForNonSkullItems
    ) {
        List<List<Optional<UiTemplateItem>>> itemGrid = createEmptyItemGrid(chestContainer.numRows);

        for (int rowIndex = 0; rowIndex < chestContainer.numRows; rowIndex++) {
            for (int colIndex = 0; colIndex < ChestContainer.COLUMNS_AMOUNT; colIndex++) {
                Optional<InventorySlot> inventorySlot = chestContainer.getGridCell(rowIndex, colIndex);
                Optional<UiTemplateItem> uiTemplateItem = UiTemplateItem.of(
                        inventorySlot.orElse(null),
                        rowIndex,
                        colIndex,
                        fillWithBlankByDefault,
                        alwaysUseMcItemNameForNonSkullItems
                );
                itemGrid.get(rowIndex).set(colIndex, uiTemplateItem);
            }
        }

        return itemGrid;
    }

    private static List<List<Optional<UiTemplateItem>>> createEmptyItemGrid(int numRows) {
        List<List<Optional<UiTemplateItem>>> itemGrid = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            itemGrid.add(new ArrayList<>());
            List<Optional<UiTemplateItem>> list = itemGrid.get(rowIndex);
            for (int colIndex = 0; colIndex < ChestContainer.COLUMNS_AMOUNT; colIndex++) {
                list.add(Optional.empty());
            }
        }

        return itemGrid;
    }

    public String formatAsTemplateCall() {
        List<String> lines = new ArrayList<>();

        String name = UI_NAME_FORMATTER.formatName(uiName);

        lines.add("{{UI|" + name);

        lines.add("|rows=" + completeUiGrid.numRows);

        if (!fillWithBlankByDefault) {
            lines.add("|fill=false");
        }

        if (completeUiGrid.uniqueCloseItem != null) {
            lines.add(completeUiGrid.uniqueCloseItem.toTemplateArgumentAsCloseItem());
        } else {
            lines.add("|close=none");
        }

        if (completeUiGrid.uniqueGoBackItem != null) {
            lines.add(completeUiGrid.uniqueGoBackItem.toTemplateArgumentAsGoBackItem());
        } else {
            lines.add("|arrow=none");
        }

        for (List<Optional<UiTemplateItem>> itemRow : completeUiGrid.itemGrid) {
            for (Optional<UiTemplateItem> uiTemplateItem : itemRow) {
                uiTemplateItem.ifPresent(item -> lines.add(item.toTemplateArgumentAsNormalItem()));
            }
        }

        lines.add("}}");

        return String.join("\n", lines);
    }
}
