package org.hsw.wikitools.feature.copy_item_tooltip;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GetItemTooltipHandlerTest {
    private void runTestExpectEmpty(Optional<Invslot> invslot) {
        FindHoveredInvslot invslotMock = new HoveredInvslotFinderStub(invslot);
        GetItemTooltipHandler classUnderTest = new GetItemTooltipHandler(invslotMock);

        Optional<GetItemTooltipHandler.GetItemTooltipResponse> inventorySlotTemplateCall =
                classUnderTest.getInventorySlotTemplateCall(new GetItemTooltipHandler.GetItemTooltipRequest());
        assertFalse(inventorySlotTemplateCall.isPresent());

        Optional<GetItemTooltipHandler.GetItemTooltipResponse> tooltipModuleDataItem =
                classUnderTest.getTooltipModuleDataItem(new GetItemTooltipHandler.GetItemTooltipRequest());
        assertFalse(tooltipModuleDataItem.isPresent());
    }

    private void runTest(Optional<Invslot> invslot, String expectedTemplateString, String expectedModuleString) {
        FindHoveredInvslot invslotMock = new HoveredInvslotFinderStub(invslot);
        GetItemTooltipHandler classUnderTest = new GetItemTooltipHandler(invslotMock);

        Optional<GetItemTooltipHandler.GetItemTooltipResponse> inventorySlotTemplateCall =
                classUnderTest.getInventorySlotTemplateCall(new GetItemTooltipHandler.GetItemTooltipRequest());
        assertTrue(inventorySlotTemplateCall.isPresent());
        String templateString = inventorySlotTemplateCall.get().tooltip;
        assertEquals(expectedTemplateString, templateString);

        Optional<GetItemTooltipHandler.GetItemTooltipResponse> tooltipModuleDataItem =
                classUnderTest.getTooltipModuleDataItem(new GetItemTooltipHandler.GetItemTooltipRequest());
        assertTrue(tooltipModuleDataItem.isPresent());
        String moduleString = tooltipModuleDataItem.get().tooltip;
        assertEquals(expectedModuleString, moduleString);
    }

    @Test
    void shouldReturnEmpty() {
        Optional<Invslot> invslot = Optional.empty();
        runTestExpectEmpty(invslot);
    }

    @Test
    void shouldReturnTooltipWithEmptyLore() {
        Optional<Invslot> invslot = Optional.of(new Invslot("Empty Lore Item", Arrays.asList()));
        String expectedTemplateString = "{{Slot|Empty Lore Item|title=Empty Lore Item}}";
        String expectedModuleString = "['Empty Lore Item'] = { name = 'Empty Lore Item', title = 'Empty Lore Item', },";
        runTest(invslot, expectedTemplateString, expectedModuleString);
    }

    @Test
    void shouldReturnMultiLineTooltip() {
        Optional<Invslot> invslot = Optional.of(new Invslot("Test Item", Arrays.asList("Lore line 1", "Lore line 2")));
        String expectedTemplateString = "{{Slot|Test Item|title=Test Item|text=Lore line 1/Lore line 2}}";
        String expectedModuleString = "['Test Item'] = { name = 'Test Item', title = 'Test Item', text = 'Lore line 1/Lore line 2', },";
        runTest(invslot, expectedTemplateString, expectedModuleString);
    }

    @Test
    void shouldFormatItem() {
        Optional<Invslot> invslot = Optional.of(new Invslot("§5Formatted Item", Arrays.asList("Line with §a green text and §b blue text")));
        String expectedTemplateString = "{{Slot|Formatted Item|title=&5Formatted Item|text=Line with &a green text and &b blue text}}";
        String expectedModuleString = "['Formatted Item'] = { name = 'Formatted Item', title = '&5Formatted Item', text = 'Line with &a green text and &b blue text', },";
        runTest(invslot, expectedTemplateString, expectedModuleString);
    }

    @Test
    void shouldFormatItemWithCorrectEscapes() {
        Optional<Invslot> invslot = Optional.of(new Invslot("Item", Arrays.asList("Comma , is not escaped but backslash \\ is.")));
        String expectedTemplateString = "{{Slot|Item|title=Item|text=Comma , is not escaped but backslash \\\\ is.}}";
        String expectedModuleString = "['Item'] = { name = 'Item', title = 'Item', text = 'Comma , is not escaped but backslash \\\\\\\\ is.', },";
        runTest(invslot, expectedTemplateString, expectedModuleString);
    }

}
