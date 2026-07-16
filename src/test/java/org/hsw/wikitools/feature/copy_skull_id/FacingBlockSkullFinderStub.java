package org.hsw.wikitools.feature.copy_skull_id;

import java.util.Optional;

public class FacingBlockSkullFinderStub implements FindFacingBlockSkull {
    public int callCount = 0;
    Optional<Skull> skull;

    public FacingBlockSkullFinderStub(Optional<Skull> skull) {
        this.skull = skull;
    }

    @Override
    public Optional<Skull> findFacingSkull() {
        callCount += 1;
        return skull;
    }
}
