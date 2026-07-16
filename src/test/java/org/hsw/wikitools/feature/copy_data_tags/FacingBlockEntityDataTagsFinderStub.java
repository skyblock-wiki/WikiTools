package org.hsw.wikitools.feature.copy_data_tags;

public class FacingBlockEntityDataTagsFinderStub implements FindFacingBlockEntityDataTags {
    public int callCount = 0;
    private final String entityDataTags;

    public FacingBlockEntityDataTagsFinderStub(String entityDataTags) {
        this.entityDataTags = entityDataTags;
    }

    @Override
    public String findFacingBlockEntityDataTags() {
        callCount += 1;
        return entityDataTags;
    }
}
