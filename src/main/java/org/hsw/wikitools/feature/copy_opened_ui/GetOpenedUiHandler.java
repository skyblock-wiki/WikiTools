package org.hsw.wikitools.feature.copy_opened_ui;

import org.jetbrains.annotations.Nullable;

public class GetOpenedUiHandler {
    private final OpenedChestContainerFinder findOpenedChestContainer;

    public GetOpenedUiHandler(OpenedChestContainerFinder findOpenedChestContainer) {
        this.findOpenedChestContainer = findOpenedChestContainer;
    }

    @Nullable
    public String getOpenedUiTemplateCall(GetOpenedUiRequest request) {
        ChestContainer chestContainer = findOpenedChestContainer.findCurrentChestContainer();

        if (chestContainer == null) {
            return null;
        }

        try {
            UiTemplateCall uiTemplateCall = UiTemplateCall.of(
                    chestContainer,
                    request.fillWithBlankByDefault,
                    request.alwaysUseMcItemNameForNonSkullItems
            );

            return uiTemplateCall.formatAsTemplateCall();

        } catch (InvalidChestContentException e) {
            throw new RuntimeException(e);  // This should not happen
        }
    }

    public record GetOpenedUiRequest(boolean fillWithBlankByDefault, boolean alwaysUseMcItemNameForNonSkullItems) {

    }
}
