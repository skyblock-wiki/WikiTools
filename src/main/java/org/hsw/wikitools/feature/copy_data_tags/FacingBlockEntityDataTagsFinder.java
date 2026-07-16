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
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class FacingBlockEntityDataTagsFinder {
    public String findFacingBlockEntityDataTags() {
        Minecraft client = Minecraft.getInstance();

        if (client.level == null) {
            return null;
        }

        if (client.hitResult == null) {
            return null;
        }

        BlockHitResult blockHitResult = (BlockHitResult)client.hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockEntity targetedBlockEntity = client.level.getBlockEntity(blockPos);

        if (targetedBlockEntity == null) {
            return null;
        }

        TagValueOutput tagValueOutput = TagValueOutput.createWithoutContext(new ProblemReporter.ScopedCollector(LogUtils.getLogger()));
        targetedBlockEntity.saveWithFullMetadata(tagValueOutput);

        String data = tagValueOutput.buildResult().toString();

        String possibleTextureValue = findGameProfile(targetedBlockEntity);
        return EntityDataTags.getEntityDataTags(data, possibleTextureValue);
    }

    @Nullable
    private static String findGameProfile(BlockEntity blockEntity) {
        if (!(blockEntity instanceof SkullBlockEntity)) {
            return null; // Not a player head
        }

        ResolvableProfile resolvableProfile = ((SkullBlockEntity) blockEntity).getOwnerProfile();

        if (resolvableProfile == null) {
            return null;
        }

        try {
            Minecraft client = Minecraft.getInstance();
            ProfileResolver profileResolver = client.services().profileResolver();
            GameProfile fullProfile = resolvableProfile.resolveProfile(profileResolver).get();
            PropertyMap propertyMap = fullProfile.properties();

            return propertyMap.toString();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return null;
    }
}
