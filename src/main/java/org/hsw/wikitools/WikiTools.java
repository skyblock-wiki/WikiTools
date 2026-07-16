package org.hsw.wikitools;

import net.fabricmc.api.ClientModInitializer;

public class WikiTools implements ClientModInitializer {
    private final FeatureRunner featureRunner = new FeatureRunner();

    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

    }

}
