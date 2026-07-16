package org.hsw.wikitools.feature.copy_opened_ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import org.hsw.wikitools.utils.ItemsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpenedChestContainerFinder {
    @Nullable
    public ChestContainer findCurrentChestContainer() {
        Screen currentScreen = Minecraft.getInstance().gui.screen();

        if (currentScreen == null) {
            return null;  // Cannot find screen
        }

        if (!(currentScreen instanceof ContainerScreen genericContainerScreen)) {
            return null;  // Not a container screen
        }

        return getChestContainerFromContainerScreen(genericContainerScreen);
    }

    private @NotNull ChestContainer getChestContainerFromContainerScreen(ContainerScreen genericContainerScreen) {
        ChestMenu screenHandler = genericContainerScreen.getMenu();
        Container inventory = screenHandler.getContainer();

        String containerName = genericContainerScreen.getTitle().getString();
        ChestContainer chestContainer = new ChestContainer(containerName, screenHandler.getRowCount());
        chestContainer.populateGrid(cellPosition -> {
            ItemStack itemStack = inventory.getItem(cellPosition.i());

            if (itemStack == ItemStack.EMPTY) {
                return Optional.empty();  // Empty item stack
            }

            return Optional.of(getInventorySlotFromItemStack(itemStack));
        });
        return chestContainer;
    }

    private @NotNull InventorySlot getInventorySlotFromItemStack(ItemStack itemStack) {
        String displayedName = ItemsUtil.formatComponentForWiki(itemStack.getHoverName());

        String minecraftItemNameInEnglish = EnglishTranslationStorage.get()
                .getOrDefault(itemStack.getItem().getDescriptionId());

        DataComponentMap components = itemStack.getComponents();

        ItemLore loreComponent = components.get(DataComponents.LORE);

        List<Component> loreTexts = new ArrayList<>();
        if (loreComponent != null) {
            loreTexts = loreComponent.styledLines();
        }

        List<String> loreLines = loreTexts.stream().map(ItemsUtil::formatComponentForWiki).toList();

        ResolvableProfile profileComponent = components.get(DataComponents.PROFILE);
        boolean isCustomSkull = profileComponent != null;

        return new InventorySlot(
                displayedName,
                minecraftItemNameInEnglish,
                loreLines,
                itemStack.getCount(),
                isCustomSkull,
                itemStack.hasFoil()
        );
    }

}
