package org.hsw.wikitools.feature.copy_item_tooltip;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetItemTooltipHandlerTest {
    private void runTestExpectEmpty() {
        FindHoveredInventorySlot inventorySlotMock = new HoveredInventorySlotFinderStub(null);
        GetItemTooltipHandler classUnderTest = new GetItemTooltipHandler(inventorySlotMock);

        String inventorySlotTemplateCall = classUnderTest.getInventorySlotTemplateCall();
        assertNull(inventorySlotTemplateCall);

        String tooltipModuleDataItem = classUnderTest.getTooltipModuleDataItem();
        assertNull(tooltipModuleDataItem);
    }

    private void runTest(TooltipInventorySlot inventorySlot, String expectedTemplateString, String expectedModuleString) {
        FindHoveredInventorySlot inventorySlotMock = new HoveredInventorySlotFinderStub(inventorySlot);
        GetItemTooltipHandler classUnderTest = new GetItemTooltipHandler(inventorySlotMock);

        String inventorySlotTemplateCall = classUnderTest.getInventorySlotTemplateCall();
        assertNotNull(inventorySlotTemplateCall);
        assertEquals(expectedTemplateString, inventorySlotTemplateCall);

        String tooltipModuleDataItem = classUnderTest.getTooltipModuleDataItem();
        assertNotNull(tooltipModuleDataItem);
        assertEquals(expectedModuleString, tooltipModuleDataItem);
    }

    @Test
    void shouldReturnEmpty() {
        runTestExpectEmpty();
    }

    @Test
    void shouldReturnTooltipWithEmptyLore() {
        TooltipInventorySlot inventorySlot = new TooltipInventorySlot("Empty Lore Item", List.of());
        String expectedTemplateString = "{{Slot|Empty Lore Item|title=Empty Lore Item}}";
        String expectedModuleString = "['Empty Lore Item'] = { name = 'Empty Lore Item', title = 'Empty Lore Item', },";
        runTest(inventorySlot, expectedTemplateString, expectedModuleString);
    }

    @Test
    void shouldReturnMultiLineTooltip() {
        TooltipInventorySlot inventorySlot = new TooltipInventorySlot("Test Item", Arrays.asList("Lore line 1", "Lore line 2"));
        String expectedTemplateString = "{{Slot|Test Item|title=Test Item|text=Lore line 1/Lore line 2}}";
        String expectedModuleString = "['Test Item'] = { name = 'Test Item', title = 'Test Item', text = 'Lore line 1/Lore line 2', },";
        runTest(inventorySlot, expectedTemplateString, expectedModuleString);
    }

    @Test
    void shouldFormatItem() {
        TooltipInventorySlot inventorySlot = new TooltipInventorySlot("§5Formatted Item", List.of("Line with §a green text and §b blue text"));
        String expectedTemplateString = "{{Slot|Formatted Item|title=&5Formatted Item|text=Line with &a green text and &b blue text}}";
        String expectedModuleString = "['Formatted Item'] = { name = 'Formatted Item', title = '&5Formatted Item', text = 'Line with &a green text and &b blue text', },";
        runTest(inventorySlot, expectedTemplateString, expectedModuleString);
    }

    @Test
    void shouldFormatItemWithCorrectEscapes() {
        TooltipInventorySlot inventorySlot = new TooltipInventorySlot("Item", List.of("Comma , is not escaped but backslash \\ is."));
        String expectedTemplateString = "{{Slot|Item|title=Item|text=Comma , is not escaped but backslash \\\\ is.}}";
        String expectedModuleString = "['Item'] = { name = 'Item', title = 'Item', text = 'Comma , is not escaped but backslash \\\\\\\\ is.', },";
        runTest(inventorySlot, expectedTemplateString, expectedModuleString);
    }

}
