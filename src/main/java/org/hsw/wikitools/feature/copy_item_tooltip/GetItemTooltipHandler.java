package org.hsw.wikitools.feature.copy_item_tooltip;

import java.util.Optional;

public class GetItemTooltipHandler {
    private final FindHoveredInvslot findHoveredInvslot;

    public GetItemTooltipHandler(FindHoveredInvslot findHoveredInvslot) {
        this.findHoveredInvslot = findHoveredInvslot;
    }

    public Optional<GetItemTooltipResponse> getInventorySlotTemplateCall(GetItemTooltipRequest request) {
        Optional<Invslot> invslot = findHoveredInvslot.findHoveredInvslot();
        if (!invslot.isPresent()) {
            return Optional.empty();
        }
        InventorySlotTemplateCall inventorySlotTemplateCall = InventorySlotTemplateCall.of(invslot.get());
        GetItemTooltipResponse response = new GetItemTooltipResponse(inventorySlotTemplateCall.tooltip);
        return Optional.of(response);
    }

    public Optional<GetItemTooltipResponse> getTooltipModuleDataItem(GetItemTooltipRequest request) {
        Optional<Invslot> invslot = findHoveredInvslot.findHoveredInvslot();
        if (!invslot.isPresent()) {
            return Optional.empty();
        }
        TooltipModuleDataItem tooltipModuleDataItem = TooltipModuleDataItem.of(invslot.get());
        GetItemTooltipResponse response = new GetItemTooltipResponse(tooltipModuleDataItem.tooltip);
        return Optional.of(response);
    }

    public static class GetItemTooltipRequest {
        public GetItemTooltipRequest() {}
    }

    public static class GetItemTooltipResponse {
        public final String tooltip;

        public GetItemTooltipResponse(String tooltip) {
            this.tooltip = tooltip;
        }
    }

}
