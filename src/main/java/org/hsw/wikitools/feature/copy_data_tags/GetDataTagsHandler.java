package org.hsw.wikitools.feature.copy_data_tags;

import java.util.Optional;

public class GetDataTagsHandler {
    private final FindHoveredItemDataTags findHoveredItemDataTags;
    private final FindFacingEntityDataTags findFacingEntityDataTags;
    private final FindFacingBlockDataTags findFacingBlockDataTags;

    public GetDataTagsHandler(FindHoveredItemDataTags findHoveredItemDataTags, FindFacingEntityDataTags findFacingEntityDataTags, FindFacingBlockDataTags findFacingBlockDataTags) {
        this.findHoveredItemDataTags = findHoveredItemDataTags;
        this.findFacingEntityDataTags = findFacingEntityDataTags;
        this.findFacingBlockDataTags = findFacingBlockDataTags;
    }

    public Optional<GetDataTagsResponse> getDataTags(GetDataTagsRequest getDataTagsRequest) {
        Optional<ItemDataTags> itemDataTags = findHoveredItemDataTags.findHoveredItemDataTags();
        if (itemDataTags.isPresent()) {
            return Optional.of(new GetDataTagsResponse(itemDataTags.get().serialized));
        }
        Optional<EntityDataTags> entityDataTags = findFacingEntityDataTags.findFacingEntityDataTags();
        if (entityDataTags.isPresent()) {
            return Optional.of(new GetDataTagsResponse(entityDataTags.get().serialized));
        }
        Optional<EntityDataTags> blockDataTags = findFacingBlockDataTags.findFacingBlockDataTags();
        if (blockDataTags.isPresent()) {
            return Optional.of(new GetDataTagsResponse(blockDataTags.get().serialized));
        }
        return Optional.empty();
    }

    public static class GetDataTagsRequest {}

    public static class GetDataTagsResponse {
        public String dataTags;

        public GetDataTagsResponse(String dataTags) {
            this.dataTags = dataTags;
        }
    }
}
