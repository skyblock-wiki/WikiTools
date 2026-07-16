package org.hsw.wikitools.feature.copy_data_tags;

import org.jetbrains.annotations.Nullable;

public class GetDataTagsHandler {
    private final HoveredItemDataTagsFinder hoveredItemDataTagsFinder;
    private final FacingEntityDataTagsFinder facingEntityDataTagsFinder;
    private final FacingBlockEntityDataTagsFinder facingBlockEntityDataTagsFinder;

    public GetDataTagsHandler(HoveredItemDataTagsFinder hoveredItemDataTagsFinder, FacingEntityDataTagsFinder facingEntityDataTagsFinder, FacingBlockEntityDataTagsFinder facingBlockEntityDataTagsFinder) {
        this.hoveredItemDataTagsFinder = hoveredItemDataTagsFinder;
        this.facingEntityDataTagsFinder = facingEntityDataTagsFinder;
        this.facingBlockEntityDataTagsFinder = facingBlockEntityDataTagsFinder;
    }

    @Nullable
    public String getDataTags() {
        String itemDataTags = hoveredItemDataTagsFinder.findHoveredItemDataTags();
        if (itemDataTags != null) {
            return itemDataTags;
        }

        String entityDataTags = facingEntityDataTagsFinder.findFacingEntityDataTags();
        if (entityDataTags != null) {
            return entityDataTags;
        }
        return facingBlockEntityDataTagsFinder.findFacingBlockEntityDataTags();
    }
}
