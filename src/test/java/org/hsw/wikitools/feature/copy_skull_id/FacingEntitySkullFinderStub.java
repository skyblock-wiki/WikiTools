package org.hsw.wikitools.feature.copy_skull_id;

public class FacingEntitySkullFinderStub implements FindFacingEntitySkull {
    public int callCount = 0;
    private final Skull skull;

    public FacingEntitySkullFinderStub(Skull skull) {
        this.skull = skull;
    }

    @Override
    public Skull findFacingSkull() {
        callCount += 1;
        return skull;
    }
}
