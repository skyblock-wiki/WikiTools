package org.hsw.wikitools.feature.copy_item_tooltip;

import org.hsw.wikitools.common.MctextTemplateFormatter;

import java.util.HashMap;

class InventorySlotTemplateCall {
    private static final MctextTemplateFormatter itemFormatter = new MctextTemplateFormatter(new HashMap<>());
    public final String tooltip;

    private InventorySlotTemplateCall(String name, String title, String text) {
        this.tooltip = toTemplateString(name, title, text);
    }

    public static InventorySlotTemplateCall of(Invslot invslot) {
        String name = itemFormatter.formatName(invslot.name);
        String title = itemFormatter.formatTitle(invslot.name);
        String loreString = itemFormatter.formatLore(invslot.lore);

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
