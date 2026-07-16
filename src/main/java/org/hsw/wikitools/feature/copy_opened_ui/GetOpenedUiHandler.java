package org.hsw.wikitools.feature.copy_opened_ui;

import java.util.Optional;

public class GetOpenedUiHandler {
    private final FindOpenedChestContainer findOpenedChestContainer;

    public GetOpenedUiHandler(FindOpenedChestContainer findOpenedChestContainer) {
        this.findOpenedChestContainer = findOpenedChestContainer;
    }

    public Optional<GetOpenedUiResponse> getOpenedUiTemplateCall(GetOpenedUiRequest request) {
        Optional<ChestContainer> chestContainer = findOpenedChestContainer.findCurrentChestContainer();

        if (!chestContainer.isPresent()) {
            return Optional.empty();
        }

        try {
            UiTemplateCall uiTemplateCall = UiTemplateCall.of(
                    chestContainer.get(),
                    request.fillWithBlankByDefault,
                    request.alwaysUseMcItemNameForNonSkullItems
            );

            String templateCall = uiTemplateCall.formatAsTemplateCall();

            return Optional.of(new GetOpenedUiResponse(templateCall));

        } catch (InvalidChestContentException e) {
            throw new RuntimeException(e);  // This should not happen
        }
    }

    public static class GetOpenedUiRequest {
        public final boolean fillWithBlankByDefault;
        public final boolean alwaysUseMcItemNameForNonSkullItems;

        public GetOpenedUiRequest(boolean fillWithBlankByDefault, boolean alwaysUseMcItemNameForNonSkullItems) {
            this.fillWithBlankByDefault = fillWithBlankByDefault;
            this.alwaysUseMcItemNameForNonSkullItems = alwaysUseMcItemNameForNonSkullItems;
        }
    }

    public static class GetOpenedUiResponse {
        public final String templateCall;

        public GetOpenedUiResponse(String templateCall) {
            this.templateCall = templateCall;
        }
    }
}
