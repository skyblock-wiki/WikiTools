package org.hsw.wikitools.feature.view_item_id;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.hsw.wikitools.mixin.common.HandledScreenAccessor;

import java.util.Optional;

public class HoveredItemIdFinder {

    public Optional<String> findHoveredItemId() {
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

        return getItemIdFromItemStack(focusedItemStack);
    }

    private Optional<String> getItemIdFromItemStack(ItemStack itemStack) {
        DataComponentMap components = itemStack.getComponents();

        CustomData nbtComponent = components.get(DataComponents.CUSTOM_DATA);

        if (nbtComponent == null) {
            return Optional.empty();  // Cannot find custom data component
        }

        CompoundTag nbtCompound = nbtComponent.copyTag();

        if (!nbtCompound.contains("id")) {
            return Optional.empty();  // Cannot find the key "id" in the custom data component
        }

        return nbtCompound.getString("id");
    }

}
