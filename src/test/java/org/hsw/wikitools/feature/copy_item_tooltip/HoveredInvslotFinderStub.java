package org.hsw.wikitools.feature.copy_item_tooltip;

import java.util.Optional;

class HoveredInvslotFinderStub implements FindHoveredInvslot {
    private final Optional<Invslot> invslot;

    public HoveredInvslotFinderStub(Optional<Invslot> invslot) {
        this.invslot = invslot;
    }

    @Override
    public Optional<Invslot> findHoveredInvslot() {
        return invslot;
    }
}
