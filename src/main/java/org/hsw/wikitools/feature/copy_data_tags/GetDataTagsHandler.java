package org.hsw.wikitools.feature.copy_data_tags;

import org.jetbrains.annotations.Nullable;

public class GetDataTagsHandler {
    private final FindHoveredItemDataTags findHoveredItemDataTags;
    private final FindFacingEntityDataTags findFacingEntityDataTags;
    private final FindFacingBlockEntityDataTags findFacingBlockEntityDataTags;

    public GetDataTagsHandler(FindHoveredItemDataTags findHoveredItemDataTags, FindFacingEntityDataTags findFacingEntityDataTags, FindFacingBlockEntityDataTags findFacingBlockEntityDataTags) {
        this.findHoveredItemDataTags = findHoveredItemDataTags;
        this.findFacingEntityDataTags = findFacingEntityDataTags;
        this.findFacingBlockEntityDataTags = findFacingBlockEntityDataTags;
    }

    @Nullable
    public String getDataTags() {
        String itemDataTags = findHoveredItemDataTags.findHoveredItemDataTags();
        if (itemDataTags != null) {
            return itemDataTags;
        }

        String entityDataTags = findFacingEntityDataTags.findFacingEntityDataTags();
        if (entityDataTags != null) {
            return entityDataTags;
        }
        return findFacingBlockEntityDataTags.findFacingBlockEntityDataTags();
    }
}
