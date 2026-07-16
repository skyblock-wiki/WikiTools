package org.hsw.wikitools.feature.copy_item_tooltip;

class TooltipModuleDataItem {
    private static final MinecraftTextModuleFormatter ITEM_FORMATTER = new MinecraftTextModuleFormatter();
    public final String tooltip;

    private TooltipModuleDataItem(String name, String title, String text) {
        this.tooltip = toModuleString(name, title, text);
    }

    public static TooltipModuleDataItem of(TooltipInventorySlot tooltipInventorySlot) {
        String name = ITEM_FORMATTER.formatName(tooltipInventorySlot.name);
        String title = ITEM_FORMATTER.formatTitle(tooltipInventorySlot.name);
        String loreString = ITEM_FORMATTER.formatLore(tooltipInventorySlot.lore);

        return new TooltipModuleDataItem(name, title, loreString);
    }

    private String toModuleString(String id, String title, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("['").append(id).append("'] = { ")
            .append("name = '").append(id).append("', ")
            .append("title = '").append(title).append("', ");
        if (text != null && !text.isEmpty()) {
            sb.append("text = '").append(text).append("', ");
        }
        sb.append("},");
        return sb.toString();
    }

}
