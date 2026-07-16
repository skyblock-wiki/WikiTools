package org.hsw.wikitools.feature.copy_data_tags;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class FacingBlockDataTagsFinder implements FindFacingBlockDataTags {
    @Override
    public Optional<EntityDataTags> findFacingBlockDataTags() {
        Minecraft client = Minecraft.getInstance();

        if (client.level == null) {
            return Optional.empty();
        }

        if (client.hitResult == null) {
            return Optional.empty();
        }


        BlockHitResult blockHitResult = (BlockHitResult)client.hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockEntity targetedBlockEntity = client.level.getBlockEntity(blockPos);

        if (targetedBlockEntity == null) {
            return Optional.empty();
        }

        TagValueOutput tagValueOutput = TagValueOutput.createWithoutContext(new ProblemReporter.ScopedCollector(LogUtils.getLogger()));
        targetedBlockEntity.saveWithFullMetadata(tagValueOutput);

        String data = tagValueOutput.buildResult().toString();

        Optional<String> textureValue = findGameProfile(targetedBlockEntity);

        EntityDataTags entityDataTags = new EntityDataTags(data, textureValue);
        return Optional.of(entityDataTags);
    }

    private static Optional<String> findGameProfile(BlockEntity blockEntity) {
        if (!(blockEntity instanceof SkullBlockEntity)) {
            return Optional.empty(); // Not a player head
        }

        ResolvableProfile resolvableProfile = ((SkullBlockEntity) blockEntity).getOwnerProfile();

        if (resolvableProfile == null) {
            return Optional.empty();
        }

        try {
            Minecraft client = Minecraft.getInstance();
            ProfileResolver profileResolver = client.services().profileResolver();
            GameProfile fullProfile = resolvableProfile.resolveProfile(profileResolver).get();
            PropertyMap propertyMap = fullProfile.properties();

//        // Extract texture value
//        Optional<String> textureValue = propertyMap.get("textures").stream().findFirst().map(Property::value);

            return Optional.of(propertyMap.toString());
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return Optional.empty();
    }
}
