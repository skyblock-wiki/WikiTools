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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class FacingBlockSkullFinder implements FindFacingBlockSkull {
    @Override
    public Optional<Skull> findFacingSkull() {
        Optional<BlockEntity> blockEntity = findFacingBlock();

        if (blockEntity.isEmpty()) {
            return Optional.empty(); // Cannot find block entity
        }

        return getSkull(blockEntity.get());
    }

    private static @NotNull Optional<BlockEntity> findFacingBlock() {
        Minecraft client = Minecraft.getInstance();

        HitResult crosshairTarget = client.hitResult;

        if (crosshairTarget == null ||
                !crosshairTarget.getType().equals(HitResult.Type.BLOCK)) {
            return Optional.empty(); // No mouseover block
        }

        if (client.level == null) {
            return Optional.empty(); // No world
        }

        if (!client.isSameThread()) {
            return Optional.empty(); // Not on thread
        }

        BlockPos blockPos = BlockPos.containing(crosshairTarget.getLocation());
        BlockEntity blockEntity = client.level.getBlockEntity(blockPos);

        return Optional.ofNullable(blockEntity);
    }

    private static @NotNull Optional<Skull> getSkull(BlockEntity blockEntity) {
        if (!(blockEntity instanceof SkullBlockEntity)) {
            return Optional.empty(); // Not a skull
        }

        ResolvableProfile profileComponent = ((SkullBlockEntity) blockEntity).getOwnerProfile();
        if (profileComponent == null) {
            return Optional.empty(); // Cannot find profile
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
            return Optional.empty(); // Cannot find textures
        }

        String textureValue = textureProperty.get().value();

        return Optional.of(Skull.ofTextureValue(textureValue));
    }
}
