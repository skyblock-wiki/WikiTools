package org.hsw.wikitools.feature.copy_data_tags;

public class FacingEntityDataTagsFinderStub extends FacingEntityDataTagsFinder {
    public int callCount = 0;
    private final String entityDataTags;

    public FacingEntityDataTagsFinderStub(String entityDataTags) {
        this.entityDataTags = entityDataTags;
    }

    @Override
    public String findFacingEntityDataTags() {
        callCount += 1;
        return entityDataTags;
    }
}
