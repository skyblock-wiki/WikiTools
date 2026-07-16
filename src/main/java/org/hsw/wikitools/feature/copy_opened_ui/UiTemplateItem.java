package org.hsw.wikitools.feature.copy_opened_ui;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class UiTemplateItem {
    public final int rowIndex;  // zero-indexed
    public final int colIndex;  // zero-indexed

    private final String templateItemValue;
    private final String loreText;

    private final boolean matchedAsCloseItem;
    private final boolean matchedAsGoBackItem;

    private UiTemplateItem(int rowIndex, int colIndex, String templateItemValue, String loreText, boolean matchedAsCloseItem, boolean matchedAsGoBackItem) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.templateItemValue = templateItemValue;
        this.loreText = loreText;
        this.matchedAsCloseItem = matchedAsCloseItem;
        this.matchedAsGoBackItem = matchedAsGoBackItem;
    }

    public static Optional<UiTemplateItem> of(
            @Nullable InventorySlot inventorySlot,
            int rowIndex,
            int colIndex,
            boolean fillWithBlankByDefault,
            boolean alwaysUseMcItemNameForNonSkullItems
    ) {
        Optional<String> templateItemValue = inventorySlot != null ?
                inventorySlot.formatAsTemplateItemValue(fillWithBlankByDefault, alwaysUseMcItemNameForNonSkullItems) :
                InventorySlot.formatEmptySlotAsTemplateItemValue(fillWithBlankByDefault);

        boolean matchedAsCloseItem = inventorySlot != null && inventorySlot.matchesAsCloseItem();
        boolean matchedAsGoBackItem = inventorySlot != null && inventorySlot.matchesAsGoBackItem();

        String loreText = inventorySlot != null ? inventorySlot.getLoreText() : "";

        return templateItemValue.map(value ->
                new UiTemplateItem(rowIndex, colIndex, value, loreText, matchedAsCloseItem, matchedAsGoBackItem));
    }

    public boolean isValidClose() {
        return matchedAsCloseItem;
    }

    public boolean isValidGoBack() {
        return !isValidClose() && matchedAsGoBackItem;
    }

    public String toTemplateArgumentAsNormalItem() {
        return "|" + getCellPosition() + "=" + templateItemValue;
    }

    public String toTemplateArgumentAsCloseItem() {
        return "|close=" + getCellPosition();
    }

    public String toTemplateArgumentAsGoBackItem() {
        String arrowLine = "|arrow=" + getCellPosition();
        String gobackLine = "|goback=" + loreText;
        return arrowLine + "\n" + gobackLine;
    }

    private String getCellPosition() {
        // |Row, Column= with top left being 1, 1
        return (rowIndex + 1) + ", " + (colIndex + 1);
    }
}
