package org.hsw.wikitools.feature.copy_item_tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.hsw.wikitools.mixin.common.HandledScreenAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HoveredInvslotFinder implements FindHoveredInvslot {

    @Override
    public Optional<Invslot> findHoveredInvslot() {
        // Implementation to find the hovered inventory slot in Minecraft
        // This method should interact with Minecraft's API to retrieve the hovered item
        // and return it as an Invslot object.

        Minecraft client = Minecraft.getInstance();
        Screen screen = client.screen;

        if (!(screen instanceof AbstractContainerScreen<?> handledScreen)) {
            return Optional.empty(); // Not a handled screen, cannot find hovered item
        }

        Slot focusedSlot = ((HandledScreenAccessor) handledScreen).getHoveredSlot();

        if (focusedSlot == null) {
            return Optional.empty(); // No focused slot, cannot find hovered item
        }

        ItemStack focusedItemStack = focusedSlot.getItem();

        if (focusedItemStack.isEmpty()) {
            return Optional.empty(); // No item hovered
        }

        Invslot focusInvslot = getInvslotFromItemStack(focusedItemStack);

        return Optional.of(focusInvslot);
    }

    private @NotNull Invslot getInvslotFromItemStack(ItemStack itemStack) {
        Component name = itemStack.getCustomName();

        if (name == null) {
            name = itemStack.getHoverName();
        }

        String itemName = toFormattedText(name);

        DataComponentMap components = itemStack.getComponents();

        ItemLore loreComponent = components.get(DataComponents.LORE);

        List<Component> loreTexts = new ArrayList<>();
        if (loreComponent != null) {
            loreTexts = loreComponent.styledLines();
        }

        List<String> loreLines = loreTexts.stream().map(HoveredInvslotFinder::toFormattedText).toList();

        return new Invslot(itemName, loreLines);
    }

    private static @NotNull String toFormattedText(Component text) {
        boolean isLeafNode = text.getSiblings().isEmpty();

        if (isLeafNode) {
            Style style = text.getStyle();
            String styleTag = toFormattedStyle(style);
            String content = text.getString();
            return styleTag + content;
        }

        List<Component> lineComponents = text.toFlatList();
        List<String> lines = lineComponents.stream().map(HoveredInvslotFinder::toFormattedText).toList();
        return String.join("", lines);
    }

    private static String toFormattedStyle(Style style) {
        // Assumption: All colors are identified by color names (not rgb values)

        StringBuilder sb = new StringBuilder();

        TextColor color = style.getColor();
        if (color != null) {
            String colorName = style.getColor().serialize();
            ChatFormatting formatting = ChatFormatting.getByName(colorName);
            if (formatting != null) {
                sb.append(formatting);
            }
        }

        if (style.isObfuscated()) {
            sb.append(ChatFormatting.OBFUSCATED);
        }

        if (style.isBold()) {
            sb.append(ChatFormatting.BOLD);
        }

        if (style.isStrikethrough()) {
            sb.append(ChatFormatting.STRIKETHROUGH);
        }

        if (style.isUnderlined()) {
            sb.append(ChatFormatting.UNDERLINE);
        }

        if (style.isItalic()) {
            sb.append(ChatFormatting.ITALIC);
        }

        return sb.toString();
    }

}
