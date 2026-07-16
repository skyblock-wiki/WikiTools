package org.hsw.wikitools.feature.copy_item_tooltip;

class HoveredInventorySlotFinderStub extends HoveredInventorySlotFinder {
    private final TooltipInventorySlot tooltipInventorySlot;

    public HoveredInventorySlotFinderStub(TooltipInventorySlot tooltipInventorySlot) {
        this.tooltipInventorySlot = tooltipInventorySlot;
    }

    @Override
    public TooltipInventorySlot findHoveredInventorySlot() {
        return tooltipInventorySlot;
    }
}
