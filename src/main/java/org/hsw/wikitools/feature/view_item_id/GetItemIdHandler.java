package org.hsw.wikitools.feature.view_item_id;

import java.util.Optional;

public class GetItemIdHandler {
    public Optional<GetItemIdResponse> getItemId(GetItemIdRequest request) {
        Optional<String> itemId = (new HoveredItemIdFinder()).findHoveredItemId();

        return itemId.map(GetItemIdResponse::new);
    }

    public static class GetItemIdRequest {}

    public static class GetItemIdResponse {
        public final String itemId;

        public GetItemIdResponse(String itemId) {
            this.itemId = itemId;
        }
    }
}
