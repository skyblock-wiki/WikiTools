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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FacingEntitySkullFinder implements FindFacingEntitySkull {
    @Override
    public Optional<Skull> findFacingSkull() {
        Minecraft client = Minecraft.getInstance();
        Entity targetedEntity = client.crosshairPickEntity;

        if (targetedEntity == null) {
            return Optional.empty(); // No mouseover entity
        }

        if (!(targetedEntity instanceof LivingEntity)) {
            return Optional.empty(); // Not living entity
        }

        return findSkull((LivingEntity) targetedEntity);
    }

    private static @NotNull Optional<Skull> findSkull(LivingEntity livingEntity) {
        ItemStack headItemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);

        if (!(headItemStack.getItem() instanceof PlayerHeadItem)) {
            return Optional.empty(); // Not a player head
        }

        DataComponentMap components = headItemStack.getComponents();

        ResolvableProfile profileComponent = components.get(DataComponents.PROFILE);

        if (profileComponent == null) {
            return Optional.empty(); // Cannot find profile component
        }

        Optional<Property> textureProperty = profileComponent.partialProfile().properties().get("textures").stream().findFirst();

        if (textureProperty.isEmpty()) {
            return Optional.empty(); // Cannot get texture property
        }

        String textureValue = textureProperty.get().value();

        return Optional.of(Skull.ofTextureValue(textureValue));
    }
}
