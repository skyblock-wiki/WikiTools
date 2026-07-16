package org.hsw.wikitools.feature.copy_skull_id;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.ResolvableProfile;
import org.hsw.wikitools.mixin.common.HandledScreenAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class HoveredSkullItemFinder implements FindHoveredSkullItem {
    @Override
    public Optional<Skull> findHoveredSkull() {
        Optional<ItemStack> focusedItemStack = findFocusedItemStack();

        if (focusedItemStack.isEmpty()) {
            return Optional.empty(); // Cannot find focused item stack
        }

        return findSkull(focusedItemStack.get());
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

    private static @NotNull Optional<Skull> findSkull(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof PlayerHeadItem)) {
            return Optional.empty(); // Not a player head
        }

        DataComponentMap components = itemStack.getComponents();

        ResolvableProfile profileComponent = components.get(DataComponents.PROFILE);

        if (profileComponent == null) {
            return Optional.empty(); // Cannot find profile component
        }

        GameProfile partialProfile = profileComponent.partialProfile();
        Optional<Property> textureProperty = partialProfile.properties().get("textures").stream().findFirst();

        if (textureProperty.isEmpty()) {
            try {
                Minecraft client = Minecraft.getInstance();
                ProfileResolver profileResolver = client.services().profileResolver();
                GameProfile fullProfile = profileComponent.resolveProfile(profileResolver).get();
                textureProperty = fullProfile.properties().get("textures").stream().findFirst();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }

        if (textureProperty.isEmpty()) {
            return Optional.empty(); // Cannot get texture property
        }

        String textureValue = textureProperty.get().value();

        return Optional.of(Skull.ofTextureValue(textureValue));
    }
}
