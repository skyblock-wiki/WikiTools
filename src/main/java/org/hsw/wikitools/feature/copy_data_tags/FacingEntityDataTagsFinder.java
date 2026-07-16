package org.hsw.wikitools.feature.copy_data_tags;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.Nullable;

public class FacingEntityDataTagsFinder {
    @Nullable
    public String findFacingEntityDataTags() {
        Minecraft client = Minecraft.getInstance();
        Entity targetedEntity = client.crosshairPickEntity;

        if (targetedEntity == null) {
            return null; // No mouseover entity
        }

        TagValueOutput tagValueOutput = TagValueOutput.createWithoutContext(new ProblemReporter.ScopedCollector(LogUtils.getLogger()));
        targetedEntity.saveWithoutId(tagValueOutput);
        String data = tagValueOutput.buildResult().toString();

        String possibleTextureValue = findGameProfile(targetedEntity);

        return EntityDataTags.getEntityDataTags(data, possibleTextureValue);
    }

    @Nullable
    private static String findGameProfile(Entity entity) {
        if (!(entity instanceof Player)) {
            return null; // Not a player entity
        }

        PropertyMap propertyMap = ((Player) entity).getGameProfile().properties();
        return propertyMap.toString();
        // Extract texture value
        // Optional<String> textureValue = propertyMap.get("textures").stream().findFirst().map(Property::value);
    }
}
