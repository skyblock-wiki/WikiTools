package org.hsw.wikitools.feature.copy_item_tooltip;

class TooltipModuleDataItem {
    private static final MinecraftTextModuleFormatter ITEM_FORMATTER = new MinecraftTextModuleFormatter();
    public final String tooltip;

    private TooltipModuleDataItem(String name, String tooltipStyle, String title, String text) {
        this.tooltip = toModuleString(name, tooltipStyle, title, text);
    }

    public static TooltipModuleDataItem of(TooltipInventorySlot tooltipInventorySlot) {
        String name = ITEM_FORMATTER.formatName(tooltipInventorySlot.name());
        String tooltipStyle = tooltipInventorySlot.tooltipStyle();
        String title = ITEM_FORMATTER.formatTitle(tooltipInventorySlot.name());
        String loreString = ITEM_FORMATTER.formatLore(tooltipInventorySlot.lore());

        return new TooltipModuleDataItem(name, tooltipStyle, title, loreString);
    }

    private String toModuleString(String id, String tooltipStyle, String title, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("['").append(id).append("'] = { ");
        sb.append("name = '").append(id).append("', ");
        if (tooltipStyle != null && !tooltipStyle.isEmpty()) {
            sb.append("tooltip_style = '").append(tooltipStyle).append("', ");
        }
        sb.append("title = '").append(title).append("', ");
        if (text != null && !text.isEmpty()) {
            sb.append("text = '").append(text).append("', ");
        }
        sb.append("},");
        return sb.toString();
    }

}
