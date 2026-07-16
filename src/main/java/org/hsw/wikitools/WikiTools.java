package org.hsw.wikitools;

import net.fabricmc.api.ClientModInitializer;
import org.hsw.wikitools.feature.copy_data_tags.*;
import org.hsw.wikitools.feature.copy_item_tooltip.CopyHoveredItemTooltipListener;
import org.hsw.wikitools.feature.copy_item_tooltip.GetItemTooltipHandler;
import org.hsw.wikitools.feature.copy_item_tooltip.HoveredInventorySlotFinder;
import org.hsw.wikitools.feature.copy_opened_ui.CopyOpenedUiListener;
import org.hsw.wikitools.feature.copy_opened_ui.GetOpenedUiHandler;
import org.hsw.wikitools.feature.copy_opened_ui.OpenedChestContainerFinder;
import org.hsw.wikitools.feature.copy_skull_id.*;
import org.hsw.wikitools.feature.mod_update_checker.GetNewVersionHandler;
import org.hsw.wikitools.feature.mod_update_checker.GithubLatestReleaseFinder;
import org.hsw.wikitools.feature.mod_update_checker.ModUpdateChecker;

public class WikiTools implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        createCopyHoveredItemTooltipListener().registerEvent();
        createCopySkullIdListener().registerEvent();
        createCopyDataTagsListener().registerEvent();
        createOpenedUiListener().registerEvent();
        createModUpdateChecker().registerEvent();
    }

    private CopyHoveredItemTooltipListener createCopyHoveredItemTooltipListener() {
        HoveredInventorySlotFinder hoveredInventorySlotFinder = new HoveredInventorySlotFinder();
        GetItemTooltipHandler getItemTooltipHandler = new GetItemTooltipHandler(hoveredInventorySlotFinder);
        return new CopyHoveredItemTooltipListener(getItemTooltipHandler);
    }

    private CopySkullIdListener createCopySkullIdListener() {
        HoveredSkullItemFinder hoveredSkullItemFinder = new HoveredSkullItemFinder();
        FacingEntitySkullFinder facingEntitySkullFinder = new FacingEntitySkullFinder();
        FacingBlockSkullFinder facingBlockSkullFinder = new FacingBlockSkullFinder();
        GetSkullIdHandler getSkullIdHandler = new GetSkullIdHandler(hoveredSkullItemFinder, facingEntitySkullFinder, facingBlockSkullFinder);
        return new CopySkullIdListener(getSkullIdHandler);
    }

    private CopyDataTagsListener createCopyDataTagsListener() {
        HoveredItemDataTagsFinder hoveredItemDataTagsFinder = new HoveredItemDataTagsFinder();
        FacingEntityDataTagsFinder facingEntityDataTagsFinder = new FacingEntityDataTagsFinder();
        FacingBlockEntityDataTagsFinder facingBlockDataTagsFinder = new FacingBlockEntityDataTagsFinder();
        GetDataTagsHandler getDataTagsHandler = new GetDataTagsHandler(hoveredItemDataTagsFinder, facingEntityDataTagsFinder, facingBlockDataTagsFinder);
        return new CopyDataTagsListener(getDataTagsHandler);
    }

    private CopyOpenedUiListener createOpenedUiListener() {
        OpenedChestContainerFinder openedChestContainerFinder = new OpenedChestContainerFinder();
        GetOpenedUiHandler getOpenedUiHandler = new GetOpenedUiHandler(openedChestContainerFinder);
        return new CopyOpenedUiListener(getOpenedUiHandler);
    }

    private ModUpdateChecker createModUpdateChecker() {
        String githubApiBaseUrl = ModProperties.GITHUB_API_BASE_URL;
        GithubLatestReleaseFinder gitHubLatestReleaseFinder = new GithubLatestReleaseFinder(githubApiBaseUrl);
        GetNewVersionHandler getNewVersionHandler = new GetNewVersionHandler(gitHubLatestReleaseFinder);
        return new ModUpdateChecker(getNewVersionHandler);
    }
}
