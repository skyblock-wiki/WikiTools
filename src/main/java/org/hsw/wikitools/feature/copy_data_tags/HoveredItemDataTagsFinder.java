package org.hsw.wikitools.feature.copy_data_tags;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.ItemStack;
import org.hsw.wikitools.utils.ItemsUtil;

public class HoveredItemDataTagsFinder {
    public String findHoveredItemDataTags() {
        ItemStack focusedItemStack = ItemsUtil.findFocusedItemStack();
        if (focusedItemStack == null) {
            return null;  // No hovered item
        }

        Minecraft client = Minecraft.getInstance();
        ClientLevel clientWorld = client.level;

        if (clientWorld == null) {
            return null; // Cannot find world
        }

        return focusedItemStack.getComponents().stream().map(TypedDataComponent::toString).reduce("", (partialString, element) -> partialString + "\n" + element);
    }
}
