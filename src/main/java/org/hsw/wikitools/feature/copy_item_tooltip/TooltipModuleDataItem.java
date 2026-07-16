package org.hsw.wikitools.feature.copy_item_tooltip;

class TooltipModuleDataItem {
    private static final MctextModuleFormatter itemFormatter = new MctextModuleFormatter();
    public final String tooltip;

    private TooltipModuleDataItem(String name, String title, String text) {
        this.tooltip = toModuleString(name, title, text);
    }

    public static TooltipModuleDataItem of(Invslot invslot) {
        String name = itemFormatter.formatName(invslot.name);
        String title = itemFormatter.formatTitle(invslot.name);
        String loreString = itemFormatter.formatLore(invslot.lore);

        return new TooltipModuleDataItem(name, title, loreString);
    }

    private String toModuleString(String name, String title, String text) {
        String id = name;
        StringBuilder sb = new StringBuilder();
        sb.append("['").append(id).append("'] = { ")
            .append("name = '").append(name).append("', ")
            .append("title = '").append(title).append("', ");
        if (text != null && !text.isEmpty()) {
            sb.append("text = '").append(text).append("', ");
        }
        sb.append("},");
        return sb.toString();
    }

}
