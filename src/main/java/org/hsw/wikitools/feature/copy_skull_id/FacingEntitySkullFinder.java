package org.hsw.wikitools.feature.copy_skull_id;

import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FacingEntitySkullFinder implements FindFacingEntitySkull {
    @Override
    @Nullable
    public Skull findFacingSkull() {
        Minecraft client = Minecraft.getInstance();
        Entity targetedEntity = client.crosshairPickEntity;

        if (targetedEntity == null) {
            return null; // No mouseover entity
        }

        if (!(targetedEntity instanceof LivingEntity)) {
            return null; // Not living entity
        }

        return findSkull((LivingEntity) targetedEntity);
    }

    @Nullable
    private static Skull findSkull(LivingEntity livingEntity) {
        ItemStack headItemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);

        if (!(headItemStack.getItem() instanceof PlayerHeadItem)) {
            return null; // Not a player head
        }

        DataComponentMap components = headItemStack.getComponents();

        ResolvableProfile profileComponent = components.get(DataComponents.PROFILE);

        if (profileComponent == null) {
            return null; // Cannot find profile component
        }

        Optional<Property> textureProperty = profileComponent.partialProfile().properties().get("textures").stream().findFirst();

        if (textureProperty.isEmpty()) {
            return null; // Cannot get texture property
        }

        String textureValue = textureProperty.get().value();

        return Skull.ofTextureValue(textureValue);
    }
}
