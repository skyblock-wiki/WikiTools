package org.hsw.wikitools.feature.copy_data_tags;

import java.util.Optional;

public class HoveredItemDataTagsFinderStub implements FindHoveredItemDataTags {
    public int callCount = 0;
    private final Optional<ItemDataTags> itemDataTags;

    public HoveredItemDataTagsFinderStub(Optional<ItemDataTags> itemDataTags) {
        this.itemDataTags = itemDataTags;
    }

    @Override
    public Optional<ItemDataTags> findHoveredItemDataTags() {
        callCount += 1;
        return itemDataTags;
    }
}
