package org.hsw.wikitools.feature.copy_item_tooltip;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.hsw.wikitools.utils.ItemsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HoveredInventorySlotFinder implements FindHoveredInventorySlot {

    @Override
    public TooltipInventorySlot findHoveredInventorySlot() {
        ItemStack focusedItemStack = ItemsUtil.findFocusedItemStack();
        return focusedItemStack == null ? null : getInventorySlotFromItemStack(focusedItemStack); // No item hovered
    }

    private @NotNull TooltipInventorySlot getInventorySlotFromItemStack(ItemStack itemStack) {
        Component name = itemStack.getCustomName();

        if (name == null) {
            name = itemStack.getHoverName();
        }

        String itemName = ItemsUtil.formatComponentForWiki(name);

        DataComponentMap components = itemStack.getComponents();
        ItemLore loreComponent = components.get(DataComponents.LORE);

        List<Component> loreTexts = new ArrayList<>();
        if (loreComponent != null) {
            loreTexts = loreComponent.styledLines();
        }

        List<String> loreLines = loreTexts.stream().map(ItemsUtil::formatComponentForWiki).toList();
        return new TooltipInventorySlot(itemName, loreLines);
    }
}
