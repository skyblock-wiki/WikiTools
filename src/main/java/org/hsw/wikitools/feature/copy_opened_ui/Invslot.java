package org.hsw.wikitools.feature.copy_opened_ui;

import org.hsw.wikitools.common.MctextTemplateFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Invslot {
    private static final HashMap<String, String> uiItemExtraEscapes = new HashMap<String, String>() {{
        put(",", "\\\\,"); // Replace , with \,
    }};
    private static final MctextTemplateFormatter uiItemFormatter =
            new MctextTemplateFormatter(uiItemExtraEscapes);

    private static final String DEFAULT_BLANK_ITEM_COLOR = "Black";

    private final String displayName;
    private final String minecraftItemNameInEnglish;
    private final List<String> lore;
    private final int stackSize;
    private final boolean isCustomSkull;
    private final boolean isEnchanted;

    public Invslot(
            String displayedName,
            String minecraftItemNameInEnglish,
            List<String> lore,
            int stackSize,
            boolean isCustomSkull,
            boolean isEnchanted
    ) {
        this.displayName = displayedName;
        this.minecraftItemNameInEnglish = minecraftItemNameInEnglish;
        this.lore = lore;
        this.stackSize = stackSize;
        this.isCustomSkull = isCustomSkull;
        this.isEnchanted = isEnchanted;
    }

    private boolean matchesAsBlankItem() {
        return minecraftItemNameInEnglish.contains("Stained Glass Pane") && displayName.equalsIgnoreCase(" ");
    }

    public boolean matchesAsCloseItem() {
        return minecraftItemNameInEnglish.equals("Barrier") && displayName.equalsIgnoreCase("§cClose");
    }

    public boolean matchesAsGoBackItem() {
        return minecraftItemNameInEnglish.equals("Arrow") && displayName.equalsIgnoreCase("§aGo Back");
    }

    public Optional<String> formatAsTemplateItemValue(
            boolean fillWithBlankByDefault,
            boolean alwaysUseMcItemNameForNonSkullItems
    ) {
        if (matchesAsBlankItem()) {
            String glassPaneColor = minecraftItemNameInEnglish.split(" ")[0];

            boolean isDefaultBlankColor = glassPaneColor.equals(DEFAULT_BLANK_ITEM_COLOR);

            if (!isDefaultBlankColor) {
                String line = formatWithFourPartExpression("Blank (" + glassPaneColor + ")");
                return Optional.of(line);
            }

            if (fillWithBlankByDefault) {
                return Optional.empty();
            }

            String line = formatWithFourPartExpression("Blank");
            return Optional.of(line);
        }

        boolean toUseMinecraftItemName = alwaysUseMcItemNameForNonSkullItems && !isCustomSkull;

        String itemName = toUseMinecraftItemName ?
                minecraftItemNameInEnglish :
                displayName;

        boolean prependNameWithEnchanted = toUseMinecraftItemName && isEnchanted;

        if (prependNameWithEnchanted) {
            itemName = "Enchanted " + itemName;
        }

        String line = formatWithFourPartExpression(itemName, displayName, lore, stackSize);

        return Optional.of(line);
    }

    public String getLoreText() {
        return uiItemFormatter.formatLore(lore);
    }

    public static Optional<String> formatEmptySlotAsTemplateItemValue(boolean fillWithBlankByDefault) {
        if (fillWithBlankByDefault) {
            return Optional.of(formatWithFourPartExpression(" "));
        }

        return Optional.empty();
    }

    private static String formatWithFourPartExpression(String itemName) {
        String name = uiItemFormatter.formatName(itemName);

        return name + ", none";
    }

    private static String formatWithFourPartExpression(String itemName, String displayedName, List<String> lore, int stackSize) {
        String name = uiItemFormatter.formatName(itemName);
        String title = uiItemFormatter.formatTitle(displayedName);
        String text = uiItemFormatter.formatLore(lore);

        boolean titleNotProvided = title.isEmpty();
        boolean textNotProvided = text.isEmpty();

        return name
                + (stackSize > 1 ? "; " + stackSize : "") + ", "
                + "none, "
                + (titleNotProvided ? "none" :
                title + ", " + (textNotProvided ? "none" : text)
        );
    }
}
