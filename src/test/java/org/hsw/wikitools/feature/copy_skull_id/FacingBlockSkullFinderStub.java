package org.hsw.wikitools.feature.copy_skull_id;

public class FacingBlockSkullFinderStub implements FindFacingBlockSkull {
    public int callCount = 0;
    private final Skull skull;

    public FacingBlockSkullFinderStub(Skull skull) {
        this.skull = skull;
    }

    @Override
    public Skull findFacingSkull() {
        callCount += 1;
        return skull;
    }
}
