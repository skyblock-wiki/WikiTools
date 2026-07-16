package org.hsw.wikitools;

import org.hsw.wikitools.feature.copy_data_tags.*;
import org.hsw.wikitools.feature.copy_item_tooltip.CopyHoveredItemTooltipListener;
import org.hsw.wikitools.feature.copy_item_tooltip.GetItemTooltipHandler;
import org.hsw.wikitools.feature.copy_item_tooltip.HoveredInvslotFinder;
import org.hsw.wikitools.feature.copy_opened_ui.CopyOpenedUiListener;
import org.hsw.wikitools.feature.copy_opened_ui.GetOpenedUiHandler;
import org.hsw.wikitools.feature.copy_opened_ui.OpenedChestContainerFinder;
import org.hsw.wikitools.feature.copy_skull_id.*;
import org.hsw.wikitools.feature.mod_update_checker.GetNewVersionHandler;
import org.hsw.wikitools.feature.mod_update_checker.GithubLatestReleaseFinder;
import org.hsw.wikitools.feature.mod_update_checker.ModUpdateChecker;

class FeatureRunner {
    private final CopyHoveredItemTooltipListener copyHoveredItemTooltipListener;
	private final CopySkullIdListener copySkullIdListener;
	private final CopyDataTagsListener copyDataTagsListener;
	private final CopyOpenedUiListener copyOpenedUiListener;
	private final ModUpdateChecker modUpdateChecker;

	public FeatureRunner() {
		copyHoveredItemTooltipListener = createCopyHoveredItemTooltipListener();
		copySkullIdListener = createCopySkullIdListener();
		copyDataTagsListener = createCopyDataTagsListener();
		copyOpenedUiListener = createOpenedUiListener();
		modUpdateChecker = createModUpdateChecker();
	}

	private CopyHoveredItemTooltipListener createCopyHoveredItemTooltipListener() {
		HoveredInvslotFinder hoveredInvslotFinder = new HoveredInvslotFinder();
		GetItemTooltipHandler getItemTooltipHandler = new GetItemTooltipHandler(hoveredInvslotFinder);
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
		FacingBlockDataTagsFinder facingBlockDataTagsFinder = new FacingBlockDataTagsFinder();
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
