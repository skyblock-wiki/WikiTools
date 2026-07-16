package org.hsw.wikitools.feature.copy_opened_ui;

import java.util.Optional;

public class OpenedChestContainerFinderStub implements FindOpenedChestContainer {
    private final Optional<ChestContainer> chestContainer;

    public OpenedChestContainerFinderStub(Optional<ChestContainer> chestContainer) {
        this.chestContainer = chestContainer;
    }

    @Override
    public Optional<ChestContainer> findCurrentChestContainer() {
        return chestContainer;
    }
}
