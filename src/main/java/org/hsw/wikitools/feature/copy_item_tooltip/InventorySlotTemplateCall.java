package org.hsw.wikitools.feature.copy_item_tooltip;

import org.hsw.wikitools.common.MctextTemplateFormatter;

import java.util.HashMap;

class InventorySlotTemplateCall {
    private static final MctextTemplateFormatter ITEM_FORMATTER = new MctextTemplateFormatter(new HashMap<>());
    public final String tooltip;

    private InventorySlotTemplateCall(String name, String title, String text) {
        this.tooltip = toTemplateString(name, title, text);
    }

    public static InventorySlotTemplateCall of(TooltipInventorySlot tooltipInventorySlot) {
        String name = ITEM_FORMATTER.formatName(tooltipInventorySlot.name());
        String title = ITEM_FORMATTER.formatTitle(tooltipInventorySlot.name());
        String loreString = ITEM_FORMATTER.formatLore(tooltipInventorySlot.lore());

        return new InventorySlotTemplateCall(name, title, loreString);
    }

    private String toTemplateString(String name, String title, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("{{Slot")
            .append("|").append(name)
            .append("|title=").append(title);
        if (text != null && !text.isEmpty()) {
            sb.append("|text=").append(text);
        }
        sb.append("}}");
        return sb.toString();
    }

}
