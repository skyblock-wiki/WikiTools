package org.hsw.wikitools.feature.copy_data_tags;

public class HoveredItemDataTagsFinderStub implements FindHoveredItemDataTags {
    public int callCount = 0;
    private final String itemDataTags;

    public HoveredItemDataTagsFinderStub(String itemDataTags) {
        this.itemDataTags = itemDataTags;
    }

    @Override
    public String findHoveredItemDataTags() {
        callCount += 1;
        return itemDataTags;
    }
}
