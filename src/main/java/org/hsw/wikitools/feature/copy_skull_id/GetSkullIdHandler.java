package org.hsw.wikitools.feature.copy_skull_id;

public class GetSkullIdHandler {
    private final FindHoveredSkullItem findHoveredSkullItem;
    private final FindFacingEntitySkull findFacingEntitySkull;
    private final FindFacingBlockSkull findFacingBlockSkull;

    public GetSkullIdHandler(FindHoveredSkullItem findHoveredSkullItem, FindFacingEntitySkull findFacingEntitySkull, FindFacingBlockSkull findFacingBlockSkull) {
        this.findHoveredSkullItem = findHoveredSkullItem;
        this.findFacingEntitySkull = findFacingEntitySkull;
        this.findFacingBlockSkull = findFacingBlockSkull;
    }

    public String getSkullId() {
        Skull hoveredSkullItem = findHoveredSkullItem.findHoveredSkull();
        if (hoveredSkullItem != null) {
            return hoveredSkullItem.textureId;
        }
        Skull facingEntitySkull = findFacingEntitySkull.findFacingSkull();
        if (facingEntitySkull != null) {
            return facingEntitySkull.textureId;
        }
        Skull facingBlockSkull = findFacingBlockSkull.findFacingSkull();
        return facingBlockSkull == null ? null : facingBlockSkull.textureId;
    }
}
