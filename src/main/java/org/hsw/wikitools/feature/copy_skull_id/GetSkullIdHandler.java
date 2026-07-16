package org.hsw.wikitools.feature.copy_skull_id;

public class GetSkullIdHandler {
    private final HoveredSkullItemFinder hoveredSkullItemFinder;
    private final FacingEntitySkullFinder entitySkullFinder;
    private final FacingBlockSkullFinder blockSkullFinder;

    public GetSkullIdHandler(HoveredSkullItemFinder hoveredSkullItemFinder, FacingEntitySkullFinder entitySkullFinder, FacingBlockSkullFinder blockSkullFinder) {
        this.hoveredSkullItemFinder = hoveredSkullItemFinder;
        this.entitySkullFinder = entitySkullFinder;
        this.blockSkullFinder = blockSkullFinder;
    }

    public String getSkullId() {
        Skull hoveredSkullItem = hoveredSkullItemFinder.findHoveredSkull();
        if (hoveredSkullItem != null) {
            return hoveredSkullItem.textureId;
        }
        Skull facingEntitySkull = entitySkullFinder.findFacingSkull();
        if (facingEntitySkull != null) {
            return facingEntitySkull.textureId;
        }
        Skull facingBlockSkull = blockSkullFinder.findFacingSkull();
        return facingBlockSkull == null ? null : facingBlockSkull.textureId;
    }
}
