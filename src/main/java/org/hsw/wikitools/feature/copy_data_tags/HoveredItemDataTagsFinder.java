package org.hsw.wikitools.feature.copy_data_tags;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.hsw.wikitools.mixin.common.HandledScreenAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HoveredItemDataTagsFinder implements FindHoveredItemDataTags {
    @Override
    public Optional<ItemDataTags> findHoveredItemDataTags() {
        Optional<ItemStack> focusedItemStack = findFocusedItemStack();

        if (focusedItemStack.isEmpty()) {
            return Optional.empty();  // No hovered item
        }

        Minecraft client = Minecraft.getInstance();
        ClientLevel clientWorld = client.level;

        if (clientWorld == null) {
            return Optional.empty(); // Cannot find world
        }

        String data = focusedItemStack.get().getComponents().stream().map(TypedDataComponent::toString)
                .reduce("", (partialString, element) -> partialString + "\n" + element);

        ItemDataTags itemDataTags = new ItemDataTags(data);
        return Optional.of(itemDataTags);
    }

    private static @NotNull Optional<ItemStack> findFocusedItemStack() {
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

        return Optional.of(focusedItemStack);
    }
}
