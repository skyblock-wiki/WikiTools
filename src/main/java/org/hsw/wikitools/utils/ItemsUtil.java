package org.hsw.wikitools.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.hsw.wikitools.mixin.common.HandledScreenAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemsUtil {

    @Nullable
    public static ItemStack findFocusedItemStack() {
        Minecraft client = Minecraft.getInstance();
        Screen screen = client.gui.screen();

        if (!(screen instanceof AbstractContainerScreen<?> handledScreen)) {
            return null; // Not a handled screen, cannot find hovered item
        }

        Slot focusedSlot = ((HandledScreenAccessor) handledScreen).getHoveredSlot();

        if (focusedSlot == null) {
            return null; // No focused slot, cannot find hovered item
        }

        ItemStack focusedItemStack = focusedSlot.getItem();
        return focusedItemStack.isEmpty() ? null : focusedItemStack;
    }

    public static @NotNull String formatComponentForWiki(Component text) {
        boolean isLeafNode = text.getSiblings().isEmpty();

        if (isLeafNode) {
            Style style = text.getStyle();
            String styleTag = toFormattedStyle(style);
            String content = text.getString();
            return styleTag + content;
        }

        List<Component> lineComponents = text.toFlatList();
        List<String> lines = lineComponents.stream().map(ItemsUtil::formatComponentForWiki).toList();
        return String.join("", lines);
    }

    private static String toFormattedStyle(Style style) {
        // Assumption: All colors are identified by color names (not rgb values)

        StringBuilder sb = new StringBuilder();

        TextColor color = style.getColor();
        if (color != null) {
            String colorName = style.getColor().serialize();
            try {
                ChatFormatting formatting = ChatFormatting.valueOf(colorName.toUpperCase());
                sb.append(formatting);
            } catch (IllegalArgumentException ignored) {}
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
