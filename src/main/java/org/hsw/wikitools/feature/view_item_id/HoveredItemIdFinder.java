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
import org.jetbrains.annotations.Nullable;

public class HoveredItemIdFinder {

    @Nullable
    public String findHoveredItemId() {
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

        return getItemIdFromItemStack(focusedItemStack);
    }

    private String getItemIdFromItemStack(ItemStack itemStack) {
        DataComponentMap components = itemStack.getComponents();

        CustomData nbtComponent = components.get(DataComponents.CUSTOM_DATA);

        if (nbtComponent == null) {
            return null;  // Cannot find custom data component
        }

        CompoundTag nbtCompound = nbtComponent.copyTag();

        if (!nbtCompound.contains("id")) {
            return null;  // Cannot find the key "id" in the custom data component
        }

        return nbtCompound.getString("id").orElse(null);
    }

}
