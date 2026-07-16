package org.hsw.wikitools.feature.copy_opened_ui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpenedChestContainerFinder implements FindOpenedChestContainer {

    @Override
    public Optional<ChestContainer> findCurrentChestContainer() {
        Screen currentScreen = Minecraft.getInstance().screen;

        if (currentScreen == null) {
            return Optional.empty();  // Cannot find screen
        }

        if (!(currentScreen instanceof ContainerScreen genericContainerScreen)) {
            return Optional.empty();  // Not a container screen
        }

        ChestContainer chestContainer = getChestContainerFromContainerScreen(genericContainerScreen);

        return Optional.of(chestContainer);
    }

    private @NotNull ChestContainer getChestContainerFromContainerScreen(ContainerScreen genericContainerScreen) {
        ChestMenu screenHandler = genericContainerScreen.getMenu();
        Container inventory = screenHandler.getContainer();

        String containerName = genericContainerScreen.getTitle().getString();
        ChestContainer chestContainer = new ChestContainer(containerName, screenHandler.getRowCount());
        chestContainer.populateGrid(cellPosition -> {
            ItemStack itemStack = inventory.getItem(cellPosition.i);

            if (itemStack == ItemStack.EMPTY) {
                return Optional.empty();  // Empty item stack
            }

            return Optional.of(getInvslotFromItemStack(itemStack));
        });
        return chestContainer;
    }

    private @NotNull Invslot getInvslotFromItemStack(ItemStack itemStack) {
        String displayedName = toFormattedText(itemStack.getHoverName());

        String minecraftItemNameInEnglish = EnglishTranslationStorage.get()
                .getOrDefault(itemStack.getItem().getDescriptionId());

        DataComponentMap components = itemStack.getComponents();

        ItemLore loreComponent = components.get(DataComponents.LORE);

        List<Component> loreTexts = new ArrayList<>();
        if (loreComponent != null) {
            loreTexts = loreComponent.styledLines();
        }

        List<String> loreLines = loreTexts.stream().map(OpenedChestContainerFinder::toFormattedText).toList();

        ResolvableProfile profileComponent = components.get(DataComponents.PROFILE);
        boolean isCustomSkull = profileComponent != null;

        return new Invslot(
                displayedName,
                minecraftItemNameInEnglish,
                loreLines,
                itemStack.getCount(),
                isCustomSkull,
                itemStack.hasFoil()
        );
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
        List<String> lines = lineComponents.stream().map(OpenedChestContainerFinder::toFormattedText).toList();
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
