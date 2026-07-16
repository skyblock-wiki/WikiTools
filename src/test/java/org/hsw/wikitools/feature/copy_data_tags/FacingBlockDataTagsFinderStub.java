package org.hsw.wikitools.feature.copy_data_tags;

import java.util.Optional;

public class FacingBlockDataTagsFinderStub implements FindFacingBlockDataTags {
    public int callCount = 0;
    private final Optional<EntityDataTags> entityDataTags;

    public FacingBlockDataTagsFinderStub(Optional<EntityDataTags> entityDataTags) {
        this.entityDataTags = entityDataTags;
    }

    @Override
    public Optional<EntityDataTags> findFacingBlockDataTags() {
        callCount += 1;
        return entityDataTags;
    }
}
