package org.hsw.wikitools.feature.copy_skull_id;

import java.util.Optional;

public class FacingEntitySkullFinderStub implements FindFacingEntitySkull {
    public int callCount = 0;
    Optional<Skull> skull;

    public FacingEntitySkullFinderStub(Optional<Skull> skull) {
        this.skull = skull;
    }

    @Override
    public Optional<Skull> findFacingSkull() {
        callCount += 1;
        return skull;
    }
}
