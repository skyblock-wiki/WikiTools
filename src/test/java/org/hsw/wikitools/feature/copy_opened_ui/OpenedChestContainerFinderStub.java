package org.hsw.wikitools.feature.copy_opened_ui;

import org.jetbrains.annotations.Nullable;

public class OpenedChestContainerFinderStub extends OpenedChestContainerFinder {
    @Nullable
    private final ChestContainer chestContainer;

    public OpenedChestContainerFinderStub(@Nullable ChestContainer chestContainer) {
        this.chestContainer = chestContainer;
    }

    @Override
    public ChestContainer findCurrentChestContainer() {
        return chestContainer;
    }
}
