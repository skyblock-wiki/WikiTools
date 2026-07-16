package org.hsw.wikitools.feature.copy_skull_id;

import java.util.Optional;

public class GetSkullIdHandler {
    private final FindHoveredSkullItem findHoveredSkullItem;
    private final FindFacingEntitySkull findFacingEntitySkull;
    private final FindFacingBlockSkull findFacingBlockSkull;

    public GetSkullIdHandler(FindHoveredSkullItem findHoveredSkullItem, FindFacingEntitySkull findFacingEntitySkull, FindFacingBlockSkull findFacingBlockSkull) {
        this.findHoveredSkullItem = findHoveredSkullItem;
        this.findFacingEntitySkull = findFacingEntitySkull;
        this.findFacingBlockSkull = findFacingBlockSkull;
    }

    public Optional<GetSkullIdResponse> getSkullId(GetSkullIdRequest request) {
        Optional<Skull> hoveredSkullItem = findHoveredSkullItem.findHoveredSkull();
        if (hoveredSkullItem.isPresent()) {
            return GetSkullIdResponse.of(hoveredSkullItem);
        }
        Optional<Skull> facingEntitySkull = findFacingEntitySkull.findFacingSkull();
        if (facingEntitySkull.isPresent()) {
            return GetSkullIdResponse.of(facingEntitySkull);
        }
        Optional<Skull> facingBlockSkull = findFacingBlockSkull.findFacingSkull();
        return GetSkullIdResponse.of(facingBlockSkull);
    }

    public static class GetSkullIdRequest {}

    public static class GetSkullIdResponse {
        public final String textureId;

        private GetSkullIdResponse(String textureId) {
            this.textureId = textureId;
        }

        public static Optional<GetSkullIdResponse> of(Optional<Skull> skull) {
            return skull.map(value -> new GetSkullIdResponse(value.textureId));
        }
    }
}
