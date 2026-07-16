package org.hsw.wikitools.feature.copy_item_tooltip;

import java.util.List;

public class TooltipInventorySlot {
    public final String name;
    public final List<String> lore;

    public TooltipInventorySlot(String name, List<String> lore) {
        this.name = name;
        this.lore = lore;
    }
}
