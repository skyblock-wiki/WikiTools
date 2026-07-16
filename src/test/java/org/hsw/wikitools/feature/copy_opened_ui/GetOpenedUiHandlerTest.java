package org.hsw.wikitools.feature.copy_opened_ui;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GetOpenedUiHandlerTest {
    private static String wrapperForUnfilledUi(List<String> inputLines) {
        List<String> finalLines = new ArrayList<>(Arrays.asList(
                "{{UI|Test UI",
                "|rows=1",
                "|fill=false",
                "|close=none",
                "|arrow=none"
        ));
        finalLines.addAll(inputLines);
        finalLines.add("}}");
        return String.join("\n", finalLines);
    }

    private static @NotNull GetOpenedUiHandler handlerOfUnfilledUiWithOneItem(InventorySlot inventorySlotForTest) {
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        chestContainer.populateGrid(cellPosition -> {
            if (cellPosition.i() == 0) {
                return Optional.of(inventorySlotForTest);
            }
            return Optional.empty();
        });
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(chestContainer);
        return new GetOpenedUiHandler(finder);
    }

    private static @NotNull GetOpenedUiHandler handlerOfUnfilledUiWithTwoItems(InventorySlot inventorySlotForTest, InventorySlot inventorySlotForTest2) {
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        chestContainer.populateGrid(cellPosition -> {
            if (cellPosition.i() == 0) {
                return Optional.of(inventorySlotForTest);
            }
            if (cellPosition.i() == 1) {
                return Optional.of(inventorySlotForTest2);
            }
            return Optional.empty();
        });
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(chestContainer);
        return new GetOpenedUiHandler(finder);
    }

    private static @NotNull GetOpenedUiHandler handlerOfAllBlankUiWithNoItem() {
        InventorySlot blankItem = new InventorySlot(
                " ",
                "Black Stained Glass Pane",
                Collections.emptyList(),
                1,
                false,
                false
        );
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        chestContainer.populateGrid(_ -> Optional.of(blankItem));
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(chestContainer);
        return new GetOpenedUiHandler(finder);
    }

    private static @NotNull GetOpenedUiHandler handlerOfAllBlankUiWithOneItem(InventorySlot inventorySlotForTest) {
        InventorySlot blankItem = new InventorySlot(
                " ",
                "Black Stained Glass Pane",
                Collections.emptyList(),
                1,
                false,
                false
        );
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        chestContainer.populateGrid(cellPosition -> {
            if (cellPosition.i() == 0) {
                return Optional.of(inventorySlotForTest);
            }
            return Optional.of(blankItem);
        });
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(chestContainer);
        return new GetOpenedUiHandler(finder);
    }

    @Test
    void noChestContainer() {
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(null);
        GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

        GetOpenedUiHandler.GetOpenedUiRequest request =
                new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
        String response = classUnderTest.getOpenedUiTemplateCall(request);

        assertNull(response);
    }

    @Test
    void uiWithNoItem() {
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(chestContainer);
        GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

        GetOpenedUiHandler.GetOpenedUiRequest request = new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
        String response = classUnderTest.getOpenedUiTemplateCall(request);

        String expected = wrapperForUnfilledUi(Collections.emptyList());
        assertNotNull(response);
        assertEquals(expected, response);
    }

    @Nested
    class TestsForNormalItem {
        @Test
        void uiWithMultipleRowsAndItems() {
            InventorySlot inventorySlotForTest = new InventorySlot(
                    "§5Formatted Item",
                    "Item",
                    Arrays.asList("Line §a green", "Line §b blue"),
                    1,
                    false,
                    false
            );
            ChestContainer chestContainer = new ChestContainer("Test UI", 2);
            chestContainer.populateGrid(cellPosition -> {
                if (Arrays.asList(0, 4, 17).contains(cellPosition.i())) {
                    return Optional.of(inventorySlotForTest);
                }
                return Optional.empty();
            });
            OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(chestContainer);
            GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

            GetOpenedUiHandler.GetOpenedUiRequest request = new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
            String response = classUnderTest.getOpenedUiTemplateCall(request);

            String expected = """
                    {{UI|Test UI
                    |rows=2
                    |fill=false
                    |close=none
                    |arrow=none
                    |1, 1=Formatted Item, none, &5Formatted Item, Line &a green/Line &b blue
                    |1, 5=Formatted Item, none, &5Formatted Item, Line &a green/Line &b blue
                    |2, 9=Formatted Item, none, &5Formatted Item, Line &a green/Line &b blue
                    }}""";
            assertNotNull(response);
            assertEquals(expected, response);
        }

        @Test
        void uiWithItemsShouldBeCorrectlyEscaped() {
            InventorySlot inventorySlotForTest = new InventorySlot(
                    "Item",
                    "Item",
                    List.of("Both comma , and backslash \\ are escaped."),
                    1,
                    false,
                    false
            );
            GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

            GetOpenedUiHandler.GetOpenedUiRequest request = new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
            String response = classUnderTest.getOpenedUiTemplateCall(request);

            String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Item, none, Item, Both comma \\, and backslash \\\\ are escaped."));
            assertNotNull(response);
            assertEquals(expected, response);
        }

        @Nested
        class ExpectStackSizeIsShown {
            @Test
            void whenStackSizeIsMoreThanOne() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        2,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Displayed Name; 2, none, Displayed Name, Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class ExpectTitleAndTextAreShownAsSingleNone {
            @Test
            void whenDisplayedNameIsEmpty() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        "",  // Empty displayed name
                        "Item",
                        Collections.singletonList("Lore that will not be displayed"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=, none, none"));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class ExpectTextIsShownAsNone {
            @Test
            void whenLoreIsEmpty() {
                InventorySlot inventorySlotUsingEmptyListAsLore = new InventorySlot(
                        "Displayed Name",
                        "Item",
                        Collections.emptyList(),  // Empty lore
                        1,
                        false,
                        false
                );
                InventorySlot inventorySlotUsingSingletonOfEmptyStringAsLore = new InventorySlot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList(""),  // Empty lore
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithTwoItems(
                        inventorySlotUsingEmptyListAsLore, inventorySlotUsingSingletonOfEmptyStringAsLore);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Arrays.asList(
                        "|1, 1=Displayed Name, none, Displayed Name, none",
                        "|1, 2=Displayed Name, none, Displayed Name, none"
                ));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class ExpectDisplayedNameIsUsedAsItemName {
            @Test
            void when_notAlwaysUseMcItemName_and_itemIsCustomSkull() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        "Custom Skull Name",
                        "Skull",
                        Collections.singletonList("Lore"),
                        1,
                        true,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Custom Skull Name, none, Custom Skull Name, Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }

            @Test
            void when_alwaysUseMcItemName_and_itemIsCustomSkull() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        "Custom Skull Name",
                        "Skull",
                        Collections.singletonList("Lore"),
                        1,
                        true,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, true);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Custom Skull Name, none, Custom Skull Name, Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class ExpectMinecraftItemNameIsUsedAsItemName {
            @Test
            void when_alwaysUseMcItemName_and_itemIsNotCustomSkull() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, true);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Item, none, Displayed Name, Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class ExpectEnchantedMinecraftItemNameIsUsedAsItemName {
            @Test
            void when_alwaysUseMcItemName_and_itemIsNotCustomSkull_and_itemIsEnchanted() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        true
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, true);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Enchanted Item, none, Displayed Name, Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }
    }

    @Nested
    class TestsForEmptyItems {
        @Nested
        class ExpectEmptyItemsNotDisplayed {
            @Test
            void whenNotFillWithBlankByDefault() {
                ChestContainer chestContainer = new ChestContainer("Test UI", 1);
                OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(chestContainer);
                GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.emptyList());
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class ExpectEmptyItemsDisplayedAsEmptyItems {
            @Test
            void whenFillWithBlankByDefault() {
                ChestContainer chestContainer = new ChestContainer("Test UI", 1);
                OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(chestContainer);
                GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(true, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = """
                        {{UI|Test UI
                        |rows=1
                        |close=none
                        |arrow=none
                        |1, 1= , none
                        |1, 2= , none
                        |1, 3= , none
                        |1, 4= , none
                        |1, 5= , none
                        |1, 6= , none
                        |1, 7= , none
                        |1, 8= , none
                        |1, 9= , none
                        }}""";
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }
    }

    @Nested
    class TestsForBlankItems {
        @Nested
        class NotABlankItem {
            @Test
            void when_itemIsUnstainedGlassPane_withBlankDisplayedName() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        " ",
                        "Glass Pane",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1= , none,  , Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }

            @Test
            void when_itemIsStainedGlassPane_withNonBlankDisplayedName() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        "Non-blank Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Non-blank Name, none, Non-blank Name, Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class ExpectBlankItemDisplayedAsBlank {
            @Test
            void when_itemIsBlackStainedGlassPane_and_notFillWithBlankByDefault() {
                InventorySlot blankItem = new InventorySlot(
                        " ",
                        "Black Stained Glass Pane",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(blankItem);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Blank, none"));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class ExpectBlankItemNotDisplayed {
            @Test
            void when_itemIsBlackStainedGlassPane_and_fillWithBlankByDefault() {
                GetOpenedUiHandler classUnderTest = handlerOfAllBlankUiWithNoItem();

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(true, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = """
                        {{UI|Test UI
                        |rows=1
                        |close=none
                        |arrow=none
                        }}""";
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class ExpectDisplayedAsBlankBracketColor {
            @Test
            void when_itemIsNonBlackStainedGlassPane_and_notFillWithBlankByDefault() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        " ",
                        "Red Stained Glass Pane",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Blank (Red), none"));
                assertNotNull(response);
                assertEquals(expected, response);
            }

            @Test
            void when_itemIsNonBlackStainedGlassPane_and_fillWithBlankByDefault() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        " ",
                        "Red Stained Glass Pane",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfAllBlankUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(true, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = """
                        {{UI|Test UI
                        |rows=1
                        |close=none
                        |arrow=none
                        |1, 1=Blank (Red), none
                        }}""";
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }
    }

    @Nested
    class TestsForCloseItems {
        private final String CLOSE_TEXT = "§cClose";

        @Nested
        class NotAUniqueCloseItem {
            @Test
            void when_itemIsNonBarrier_withCloseText() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        CLOSE_TEXT,
                        "Not A Barrier",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Close, none, &cClose, Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }

            @Test
            void when_itemIsBarrier_withNonCloseText() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        "Not A Close Text",
                        "Barrier",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Not A Close Text, none, Not A Close Text, Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }

            @Test
            void when_itemIsBarrier_withCloseText_andIsNotUnique() {
                InventorySlot closeItem = new InventorySlot(
                        CLOSE_TEXT,
                        "Barrier",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithTwoItems(closeItem, closeItem);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Arrays.asList(
                        "|1, 1=Close, none, &cClose, Lore",
                        "|1, 2=Close, none, &cClose, Lore"
                ));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class Expect_UseOfCloseParameter_AndNot_GridPositionParameter {
            @Test
            void when_itemIsBarrier_withCloseText_andIsUnique() {
                InventorySlot closeItem = new InventorySlot(
                        CLOSE_TEXT,
                        "Barrier",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                InventorySlot anotherItem = new InventorySlot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithTwoItems(closeItem, anotherItem);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = """
                        {{UI|Test UI
                        |rows=1
                        |fill=false
                        |close=1, 1
                        |arrow=none
                        |1, 2=Displayed Name, none, Displayed Name, Lore
                        }}""";
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }
    }

    @Nested
    class TestsForGoBackItems {
        private final String GO_BACK_TEXT = "§aGo Back";

        @Nested
        class NotAUniqueGoBackItem {
            @Test
            void when_itemIsNonArrow_withGoBackText() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        GO_BACK_TEXT,
                        "Not An Arrow",
                        Collections.singletonList("To Lobby"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Go Back, none, &aGo Back, To Lobby"));
                assertNotNull(response);
                assertEquals(expected, response);
            }

            @Test
            void when_itemIsArrow_withNonGoBackText() {
                InventorySlot inventorySlotForTest = new InventorySlot(
                        "Not A Go-Back Text",
                        "Arrow",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(inventorySlotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Not A Go-Back Text, none, Not A Go-Back Text, Lore"));
                assertNotNull(response);
                assertEquals(expected, response);
            }

            @Test
            void when_itemIsArrow_withGoBackText_andIsNotUnique() {
                InventorySlot closeItem = new InventorySlot(
                        GO_BACK_TEXT,
                        "Arrow",
                        Collections.singletonList("To Lobby"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithTwoItems(closeItem, closeItem);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Arrays.asList(
                        "|1, 1=Go Back, none, &aGo Back, To Lobby",
                        "|1, 2=Go Back, none, &aGo Back, To Lobby"
                ));
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }

        @Nested
        class Expect_UseOfArrowAndGoBackParameters_AndNot_GridPositionParameter {
            @Test
            void when_itemIsArrow_withGoBackText_andIsUnique() {
                InventorySlot closeItem = new InventorySlot(
                        GO_BACK_TEXT,
                        "Arrow",
                        Collections.singletonList("To Lobby"),
                        1,
                        false,
                        false
                );
                InventorySlot anotherItem = new InventorySlot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithTwoItems(closeItem, anotherItem);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                String response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = """
                        {{UI|Test UI
                        |rows=1
                        |fill=false
                        |close=none
                        |arrow=1, 1
                        |goback=To Lobby
                        |1, 2=Displayed Name, none, Displayed Name, Lore
                        }}""";
                assertNotNull(response);
                assertEquals(expected, response);
            }
        }
    }

}
