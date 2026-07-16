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

    private static @NotNull GetOpenedUiHandler handlerOfUnfilledUiWithOneItem(Invslot invslotForTest) {
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        chestContainer.populateGrid(cellPosition -> {
            if (cellPosition.i == 0) {
                return Optional.of(invslotForTest);
            }
            return Optional.empty();
        });
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(Optional.of(chestContainer));
        return new GetOpenedUiHandler(finder);
    }

    private static @NotNull GetOpenedUiHandler handlerOfUnfilledUiWithTwoItems(Invslot invslotForTest, Invslot invslotForTest2) {
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        chestContainer.populateGrid(cellPosition -> {
            if (cellPosition.i == 0) {
                return Optional.of(invslotForTest);
            }
            if (cellPosition.i == 1) {
                return Optional.of(invslotForTest2);
            }
            return Optional.empty();
        });
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(Optional.of(chestContainer));
        return new GetOpenedUiHandler(finder);
    }

    private static @NotNull GetOpenedUiHandler handlerOfAllBlankUiWithNoItem() {
        Invslot blankItem = new Invslot(
                " ",
                "Black Stained Glass Pane",
                Collections.emptyList(),
                1,
                false,
                false
        );
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        chestContainer.populateGrid(cellPosition -> Optional.of(blankItem));
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(Optional.of(chestContainer));
        return new GetOpenedUiHandler(finder);
    }

    private static @NotNull GetOpenedUiHandler handlerOfAllBlankUiWithOneItem(Invslot invslotForTest) {
        Invslot blankItem = new Invslot(
                " ",
                "Black Stained Glass Pane",
                Collections.emptyList(),
                1,
                false,
                false
        );
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        chestContainer.populateGrid(cellPosition -> {
            if (cellPosition.i == 0) {
                return Optional.of(invslotForTest);
            }
            return Optional.of(blankItem);
        });
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(Optional.of(chestContainer));
        return new GetOpenedUiHandler(finder);
    }

    @Test
    void noChestContainer() {
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(Optional.empty());
        GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

        GetOpenedUiHandler.GetOpenedUiRequest request =
                new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
        Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

        assertFalse(response.isPresent());
    }

    @Test
    void uiWithNoItem() {
        ChestContainer chestContainer = new ChestContainer("Test UI", 1);
        OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(Optional.of(chestContainer));
        GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

        GetOpenedUiHandler.GetOpenedUiRequest request =
                new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
        Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

        String expected = wrapperForUnfilledUi(Collections.emptyList());
        assertTrue(response.isPresent());
        assertEquals(expected, response.get().templateCall);
    }

    @Nested
    class TestsForNormalItem {
        @Test
        void uiWithMultipleRowsAndItems() {
            Invslot invslotForTest = new Invslot(
                    "§5Formatted Item",
                    "Item",
                    Arrays.asList("Line §a green", "Line §b blue"),
                    1,
                    false,
                    false
            );
            ChestContainer chestContainer = new ChestContainer("Test UI", 2);
            chestContainer.populateGrid(cellPosition -> {
                if (Arrays.asList(0, 4, 17).contains(cellPosition.i)) {
                    return Optional.of(invslotForTest);
                }
                return Optional.empty();
            });
            OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(Optional.of(chestContainer));
            GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

            GetOpenedUiHandler.GetOpenedUiRequest request =
                    new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
            Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

            String expected = "{{UI|Test UI\n" +
                    "|rows=2\n" +
                    "|fill=false\n" +
                    "|close=none\n" +
                    "|arrow=none\n" +
                    "|1, 1=Formatted Item, none, &5Formatted Item, Line &a green/Line &b blue\n" +
                    "|1, 5=Formatted Item, none, &5Formatted Item, Line &a green/Line &b blue\n" +
                    "|2, 9=Formatted Item, none, &5Formatted Item, Line &a green/Line &b blue\n" +
                    "}}";
            assertTrue(response.isPresent());
            assertEquals(expected, response.get().templateCall);
        }

        @Test
        void uiWithItemsShouldBeCorrectlyEscaped() {
            Invslot invslotForTest = new Invslot(
                    "Item",
                    "Item",
                    Arrays.asList("Both comma , and backslash \\ are escaped."),
                    1,
                    false,
                    false
            );
            GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

            GetOpenedUiHandler.GetOpenedUiRequest request =
                    new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
            Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

            String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Item, none, Item, Both comma \\, and backslash \\\\ are escaped."));
            assertTrue(response.isPresent());
            assertEquals(expected, response.get().templateCall);
        }

        @Nested
        class ExpectStackSizeIsShown {
            @Test
            void whenStackSizeIsMoreThanOne() {
                Invslot invslotForTest = new Invslot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        2,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Displayed Name; 2, none, Displayed Name, Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class ExpectTitleAndTextAreShownAsSingleNone {
            @Test
            void whenDisplayedNameIsEmpty() {
                Invslot invslotForTest = new Invslot(
                        "",  // Empty displayed name
                        "Item",
                        Collections.singletonList("Lore that will not be displayed"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=, none, none"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class ExpectTextIsShownAsNone {
            @Test
            void whenLoreIsEmpty() {
                Invslot invslotUsingEmptyListAsLore = new Invslot(
                        "Displayed Name",
                        "Item",
                        Collections.emptyList(),  // Empty lore
                        1,
                        false,
                        false
                );
                Invslot invslotUsingSingletonOfEmptyStringAsLore = new Invslot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList(""),  // Empty lore
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithTwoItems(
                        invslotUsingEmptyListAsLore, invslotUsingSingletonOfEmptyStringAsLore);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Arrays.asList(
                        "|1, 1=Displayed Name, none, Displayed Name, none",
                        "|1, 2=Displayed Name, none, Displayed Name, none"
                ));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class ExpectDisplayedNameIsUsedAsItemName {
            @Test
            void when_notAlwaysUseMcItemName_and_itemIsCustomSkull() {
                Invslot invslotForTest = new Invslot(
                        "Custom Skull Name",
                        "Skull",
                        Collections.singletonList("Lore"),
                        1,
                        true,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Custom Skull Name, none, Custom Skull Name, Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }

            @Test
            void when_alwaysUseMcItemName_and_itemIsCustomSkull() {
                Invslot invslotForTest = new Invslot(
                        "Custom Skull Name",
                        "Skull",
                        Collections.singletonList("Lore"),
                        1,
                        true,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, true);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Custom Skull Name, none, Custom Skull Name, Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class ExpectMinecraftItemNameIsUsedAsItemName {
            @Test
            void when_alwaysUseMcItemName_and_itemIsNotCustomSkull() {
                Invslot invslotForTest = new Invslot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, true);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Item, none, Displayed Name, Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class ExpectEnchantedMinecraftItemNameIsUsedAsItemName {
            @Test
            void when_alwaysUseMcItemName_and_itemIsNotCustomSkull_and_itemIsEnchanted() {
                Invslot invslotForTest = new Invslot(
                        "Displayed Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        true
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, true);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Enchanted Item, none, Displayed Name, Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
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
                OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(Optional.of(chestContainer));
                GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.emptyList());
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class ExpectEmptyItemsDisplayedAsEmptyItems {
            @Test
            void whenFillWithBlankByDefault() {
                ChestContainer chestContainer = new ChestContainer("Test UI", 1);
                OpenedChestContainerFinderStub finder = new OpenedChestContainerFinderStub(Optional.of(chestContainer));
                GetOpenedUiHandler classUnderTest = new GetOpenedUiHandler(finder);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(true, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = "{{UI|Test UI\n" +
                        "|rows=1\n" +
                        "|close=none\n" +
                        "|arrow=none\n" +
                        "|1, 1= , none\n" +
                        "|1, 2= , none\n" +
                        "|1, 3= , none\n" +
                        "|1, 4= , none\n" +
                        "|1, 5= , none\n" +
                        "|1, 6= , none\n" +
                        "|1, 7= , none\n" +
                        "|1, 8= , none\n" +
                        "|1, 9= , none\n" +
                        "}}";
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }
    }

    @Nested
    class TestsForBlankItems {
        @Nested
        class NotABlankItem {
            @Test
            void when_itemIsUnstainedGlassPane_withBlankDisplayedName() {
                Invslot invslotForTest = new Invslot(
                        " ",
                        "Glass Pane",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1= , none,  , Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }

            @Test
            void when_itemIsStainedGlassPane_withNonBlankDisplayedName() {
                Invslot invslotForTest = new Invslot(
                        "Non-blank Name",
                        "Item",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Non-blank Name, none, Non-blank Name, Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class ExpectBlankItemDisplayedAsBlank {
            @Test
            void when_itemIsBlackStainedGlassPane_and_notFillWithBlankByDefault() {
                Invslot blankItem = new Invslot(
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
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Blank, none"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class ExpectBlankItemNotDisplayed {
            @Test
            void when_itemIsBlackStainedGlassPane_and_fillWithBlankByDefault() {
                GetOpenedUiHandler classUnderTest = handlerOfAllBlankUiWithNoItem();

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(true, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = "{{UI|Test UI\n" +
                        "|rows=1\n" +
                        "|close=none\n" +
                        "|arrow=none\n" +
                        "}}";
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class ExpectDisplayedAsBlankBracketColor {
            @Test
            void when_itemIsNonBlackStainedGlassPane_and_notFillWithBlankByDefault() {
                Invslot invslotForTest = new Invslot(
                        " ",
                        "Red Stained Glass Pane",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Blank (Red), none"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }

            @Test
            void when_itemIsNonBlackStainedGlassPane_and_fillWithBlankByDefault() {
                Invslot invslotForTest = new Invslot(
                        " ",
                        "Red Stained Glass Pane",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfAllBlankUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(true, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = "{{UI|Test UI\n" +
                        "|rows=1\n" +
                        "|close=none\n" +
                        "|arrow=none\n" +
                        "|1, 1=Blank (Red), none\n" +
                        "}}";
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
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
                Invslot invslotForTest = new Invslot(
                        CLOSE_TEXT,
                        "Not A Barrier",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Close, none, &cClose, Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }

            @Test
            void when_itemIsBarrier_withNonCloseText() {
                Invslot invslotForTest = new Invslot(
                        "Not A Close Text",
                        "Barrier",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Not A Close Text, none, Not A Close Text, Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }

            @Test
            void when_itemIsBarrier_withCloseText_andIsNotUnique() {
                Invslot closeItem = new Invslot(
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
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Arrays.asList(
                        "|1, 1=Close, none, &cClose, Lore",
                        "|1, 2=Close, none, &cClose, Lore"
                ));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class Expect_UseOfCloseParameter_AndNot_GridPositionParameter {
            @Test
            void when_itemIsBarrier_withCloseText_andIsUnique() {
                Invslot closeItem = new Invslot(
                        CLOSE_TEXT,
                        "Barrier",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                Invslot anotherItem = new Invslot(
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
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = "{{UI|Test UI\n" +
                        "|rows=1\n" +
                        "|fill=false\n" +
                        "|close=1, 1\n" +
                        "|arrow=none\n" +
                        "|1, 2=Displayed Name, none, Displayed Name, Lore\n" +
                        "}}";
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
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
                Invslot invslotForTest = new Invslot(
                        GO_BACK_TEXT,
                        "Not An Arrow",
                        Collections.singletonList("To Lobby"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Go Back, none, &aGo Back, To Lobby"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }

            @Test
            void when_itemIsArrow_withNonGoBackText() {
                Invslot invslotForTest = new Invslot(
                        "Not A Go-Back Text",
                        "Arrow",
                        Collections.singletonList("Lore"),
                        1,
                        false,
                        false
                );
                GetOpenedUiHandler classUnderTest = handlerOfUnfilledUiWithOneItem(invslotForTest);

                GetOpenedUiHandler.GetOpenedUiRequest request =
                        new GetOpenedUiHandler.GetOpenedUiRequest(false, false);
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Collections.singletonList("|1, 1=Not A Go-Back Text, none, Not A Go-Back Text, Lore"));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }

            @Test
            void when_itemIsArrow_withGoBackText_andIsNotUnique() {
                Invslot closeItem = new Invslot(
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
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = wrapperForUnfilledUi(Arrays.asList(
                        "|1, 1=Go Back, none, &aGo Back, To Lobby",
                        "|1, 2=Go Back, none, &aGo Back, To Lobby"
                ));
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }

        @Nested
        class Expect_UseOfArrowAndGoBackParameters_AndNot_GridPositionParameter {
            @Test
            void when_itemIsArrow_withGoBackText_andIsUnique() {
                Invslot closeItem = new Invslot(
                        GO_BACK_TEXT,
                        "Arrow",
                        Collections.singletonList("To Lobby"),
                        1,
                        false,
                        false
                );
                Invslot anotherItem = new Invslot(
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
                Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = classUnderTest.getOpenedUiTemplateCall(request);

                String expected = "{{UI|Test UI\n" +
                        "|rows=1\n" +
                        "|fill=false\n" +
                        "|close=none\n" +
                        "|arrow=1, 1\n" +
                        "|goback=To Lobby\n" +
                        "|1, 2=Displayed Name, none, Displayed Name, Lore\n" +
                        "}}";
                assertTrue(response.isPresent());
                assertEquals(expected, response.get().templateCall);
            }
        }
    }

}
