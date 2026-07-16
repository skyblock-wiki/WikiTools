package org.hsw.wikitools.feature.copy_opened_ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.server.packs.resources.ResourceManager;

class EnglishTranslationStorage {
    private static @Nullable ClientLanguage translationStorage = null;

    public static @NotNull ClientLanguage get() {
        if (EnglishTranslationStorage.translationStorage != null) {
            return EnglishTranslationStorage.translationStorage;
        }

        ClientLanguage translationStorage = createEnglishTranslationStorage();
        EnglishTranslationStorage.translationStorage = translationStorage;
        return translationStorage;
    }

    private static ClientLanguage createEnglishTranslationStorage() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        final LanguageInfo ENGLISH_US = new LanguageInfo("US", "English", false);

        List<String> list = Collections.singletonList("en_us");

        boolean bl = ENGLISH_US.bidirectional();

        return ClientLanguage.loadFrom(resourceManager, list, bl);
    }

}
