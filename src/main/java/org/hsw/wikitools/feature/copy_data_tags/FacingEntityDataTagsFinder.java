package org.hsw.wikitools.feature.copy_data_tags;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.Optional;

public class FacingEntityDataTagsFinder implements FindFacingEntityDataTags {
    @Override
    public Optional<EntityDataTags> findFacingEntityDataTags() {
        Minecraft client = Minecraft.getInstance();
        Entity targetedEntity = client.crosshairPickEntity;

        if (targetedEntity == null) {
            return Optional.empty(); // No mouseover entity
        }

        TagValueOutput tagValueOutput = TagValueOutput.createWithoutContext(new ProblemReporter.ScopedCollector(LogUtils.getLogger()));
        targetedEntity.saveWithoutId(tagValueOutput);
        String data = tagValueOutput.buildResult().toString();

        Optional<String> textureValue = findGameProfile(targetedEntity);

        EntityDataTags entityDataTags = new EntityDataTags(data, textureValue);
        return Optional.of(entityDataTags);
    }

    private static Optional<String> findGameProfile(Entity entity) {
        if (!(entity instanceof Player)) {
            return Optional.empty(); // Not a player entity
        }

        PropertyMap propertyMap = ((Player) entity).getGameProfile().properties();

//        // Extract texture value
//        Optional<String> textureValue = propertyMap.get("textures").stream().findFirst().map(Property::value);

        return Optional.of(propertyMap.toString());
    }
}
