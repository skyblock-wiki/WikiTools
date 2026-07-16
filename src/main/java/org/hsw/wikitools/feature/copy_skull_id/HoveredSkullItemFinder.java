package org.hsw.wikitools.feature.copy_skull_id;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.ResolvableProfile;
import org.hsw.wikitools.utils.ItemsUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class HoveredSkullItemFinder {
    @Nullable
    public Skull findHoveredSkull() {
        ItemStack focusedItemStack = ItemsUtil.findFocusedItemStack();
        return focusedItemStack == null ? null : findSkull(focusedItemStack); // No item hovered
    }

    @Nullable
    private static Skull findSkull(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof PlayerHeadItem)) {
            return null; // Not a player head
        }

        DataComponentMap components = itemStack.getComponents();

        ResolvableProfile profileComponent = components.get(DataComponents.PROFILE);

        if (profileComponent == null) {
            return null; // Cannot find profile component
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
            return null; // Cannot get texture property
        }

        String textureValue = textureProperty.get().value();
        return Skull.ofTextureValue(textureValue);
    }
}
