package org.hsw.wikitools.feature.mod_update_checker;

public class LatestReleaseFinderStub implements FindModVersion {
    private final FindModVersionResult result;

    public LatestReleaseFinderStub(FindModVersionResult result) {
        this.result = result;
    }

    @Override
    public FindModVersionResult findLatestVersion() {
        return result;
    }
}
