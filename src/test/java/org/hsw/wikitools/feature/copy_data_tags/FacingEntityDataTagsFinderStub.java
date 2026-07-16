package org.hsw.wikitools.feature.copy_data_tags;

import java.util.Optional;

public class FacingEntityDataTagsFinderStub implements FindFacingEntityDataTags {
    public int callCount = 0;
    private final Optional<EntityDataTags> entityDataTags;

    public FacingEntityDataTagsFinderStub(Optional<EntityDataTags> entityDataTags) {
        this.entityDataTags = entityDataTags;
    }

    @Override
    public Optional<EntityDataTags> findFacingEntityDataTags() {
        callCount += 1;
        return entityDataTags;
    }
}
