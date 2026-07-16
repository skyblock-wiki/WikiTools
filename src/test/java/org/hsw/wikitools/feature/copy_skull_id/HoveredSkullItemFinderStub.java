package org.hsw.wikitools.feature.copy_skull_id;

public class HoveredSkullItemFinderStub extends HoveredSkullItemFinder {
    public int callCount = 0;
    private final Skull skull;

    public HoveredSkullItemFinderStub(Skull skull) {
        this.skull = skull;
    }

    @Override
    public Skull findHoveredSkull() {
        callCount += 1;
        return skull;
    }
}
