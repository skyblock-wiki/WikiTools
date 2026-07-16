package org.hsw.wikitools.feature.copy_item_tooltip;

import org.jetbrains.annotations.Nullable;

public class GetItemTooltipHandler {
    private final HoveredInventorySlotFinder hoveredInventorySlotFinder;

    public GetItemTooltipHandler(HoveredInventorySlotFinder hoveredInventorySlotFinder) {
        this.hoveredInventorySlotFinder = hoveredInventorySlotFinder;
    }

    @Nullable
    public String getInventorySlotTemplateCall() {
        TooltipInventorySlot inventorySlot = hoveredInventorySlotFinder.findHoveredInventorySlot();
        if (inventorySlot == null) {
            return null;
        }

        InventorySlotTemplateCall inventorySlotTemplateCall = InventorySlotTemplateCall.of(inventorySlot);
        return inventorySlotTemplateCall.tooltip;
    }

    @Nullable
    public String getTooltipModuleDataItem() {
        TooltipInventorySlot inventorySlot = hoveredInventorySlotFinder.findHoveredInventorySlot();
        if (inventorySlot == null) {
            return null;
        }
        TooltipModuleDataItem tooltipModuleDataItem = TooltipModuleDataItem.of(inventorySlot);
        return tooltipModuleDataItem.tooltip;
    }
}
