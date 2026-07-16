package org.hsw.wikitools.feature.copy_skull_id;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class FacingBlockSkullFinder implements FindFacingBlockSkull {
    @Override
    @Nullable
    public Skull findFacingSkull() {
        BlockEntity blockEntity = findFacingBlock();
        return blockEntity == null ? null : getSkull(blockEntity);
    }

    @Nullable
    private static BlockEntity findFacingBlock() {
        Minecraft client = Minecraft.getInstance();

        HitResult crosshairTarget = client.hitResult;

        if (crosshairTarget == null || !crosshairTarget.getType().equals(HitResult.Type.BLOCK)) {
            return null; // No mouseover block
        }

        if (client.level == null) {
            return null; // No world
        }

        if (!client.isSameThread()) {
            return null; // Not on thread
        }

        BlockPos blockPos = BlockPos.containing(crosshairTarget.getLocation());
        return client.level.getBlockEntity(blockPos);
    }

    @Nullable
    private static Skull getSkull(BlockEntity blockEntity) {
        if (!(blockEntity instanceof SkullBlockEntity)) {
            return null; // Not a skull
        }

        ResolvableProfile profileComponent = ((SkullBlockEntity) blockEntity).getOwnerProfile();
        if (profileComponent == null) {
            return null; // Cannot find profile
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
            return null; // Cannot find textures
        }

        String textureValue = textureProperty.get().value();
        return Skull.ofTextureValue(textureValue);
    }
}
