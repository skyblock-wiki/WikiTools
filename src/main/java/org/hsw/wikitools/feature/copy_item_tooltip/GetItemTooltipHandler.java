package org.hsw.wikitools.feature.copy_item_tooltip;

import org.jetbrains.annotations.Nullable;

public class GetItemTooltipHandler {
    private final FindHoveredInventorySlot findHoveredInventorySlot;

    public GetItemTooltipHandler(FindHoveredInventorySlot findHoveredInventorySlot) {
        this.findHoveredInventorySlot = findHoveredInventorySlot;
    }

    @Nullable
    public String getInventorySlotTemplateCall() {
        TooltipInventorySlot inventorySlot = findHoveredInventorySlot.findHoveredInventorySlot();
        if (inventorySlot == null) {
            return null;
        }

        InventorySlotTemplateCall inventorySlotTemplateCall = InventorySlotTemplateCall.of(inventorySlot);
        return inventorySlotTemplateCall.tooltip;
    }

    @Nullable
    public String getTooltipModuleDataItem() {
        TooltipInventorySlot inventorySlot = findHoveredInventorySlot.findHoveredInventorySlot();
        if (inventorySlot == null) {
            return null;
        }
        TooltipModuleDataItem tooltipModuleDataItem = TooltipModuleDataItem.of(inventorySlot);
        return tooltipModuleDataItem.tooltip;
    }
}
