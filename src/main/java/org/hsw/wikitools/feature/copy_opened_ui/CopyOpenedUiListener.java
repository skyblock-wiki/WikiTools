package org.hsw.wikitools.feature.copy_opened_ui;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.hsw.wikitools.common.ClipboardHelper;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hsw.wikitools.ModProperties.CATEGORY;

public class CopyOpenedUiListener {
    protected static final int CRAFTING_TABLE_OR_RECIPE_REQUIRED_SLOT = 23;
    protected static final int[][] CRAFTING_TABLE_INGREDIENT_SLOTS = {{10, 19, 28}, {11, 20, 29}, {12, 21, 30}};

    private final GetOpenedUiHandler getOpenedUiHandler;
    private final KeyMapping copyOpenedUiKeybinding;

    public CopyOpenedUiListener(GetOpenedUiHandler getOpenedUiHandler) {
        this.getOpenedUiHandler = getOpenedUiHandler;
        this.copyOpenedUiKeybinding = registerKeyBinding();
    }

    private KeyMapping registerKeyBinding() {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.wikitools.copy_opened_ui",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                CATEGORY
        ));
    }

    public void registerEvent() {
        // Add a listener for screen events (when a screen is opened)
        // to add a keyboard event listener for the HandledScreen

        ScreenEvents.BEFORE_INIT.register((client, screen, _, _) -> {
            // Add a listener for keyboard events if the screen is a HandledScreen

            if (screen instanceof AbstractContainerScreen<?>) {
                ScreenKeyboardEvents.afterKeyPress(screen).register((_, keyInput) -> onKeyPress(client, keyInput));
            }
        });
    }

    private void onKeyPress(Minecraft client, KeyEvent keyInput) {
        boolean cIsPressed = copyOpenedUiKeybinding.matches(keyInput);
        if (cIsPressed && processPossibleRecipeMenu()) {
            return;
        }

        boolean cIsPressedWithShift = cIsPressed && (keyInput.hasShiftDown());
        boolean cIsPressedWithControl = cIsPressed && (keyInput.hasControlDown());

        if (cIsPressed) {
            copyOpenedUiTemplateCall(client, cIsPressedWithShift, cIsPressedWithControl);
        }
    }

    private boolean processPossibleRecipeMenu() {
        Screen screen = Minecraft.getInstance().gui.screen();

        if (screen instanceof ContainerScreen containerScreen) {
            List<ItemStack> items = containerScreen.getMenu().getItems();
            int rows = containerScreen.getMenu().getRowCount();
            String inventoryName = screen.getTitle().getString();

            if (isCraftingRecipeMenu(rows, inventoryName, items)) {
                processRecipe(new StringBuilder(), inventoryName, items);
                return true;
            }
        }

        return false;
    }

    protected boolean isCraftingRecipeMenu(int rows, String inventoryName, List<ItemStack> items) {
        if (rows == 6 && !items.get(CRAFTING_TABLE_OR_RECIPE_REQUIRED_SLOT).isEmpty()) {
            ItemStack craftingItem = items.get(CRAFTING_TABLE_OR_RECIPE_REQUIRED_SLOT);
            return (craftingItem.getItem() == Items.BARRIER && inventoryName.contains("Craft Item")) || craftingItem.getItem() == Items.CRAFTING_TABLE;
        }
        return false;
    }

    protected void processRecipe(StringBuilder builder, String inventoryName, List<ItemStack> items) {
        boolean craftItem = inventoryName.equalsIgnoreCase("Craft Item");
        ItemStack product = craftItem ? items.get(23) : items.get(25);

        builder.append("{{Crafting Recipe Table\n|{{Crafting Recipe Row\n |requirement = ADD HERE OR DELETE THIS");

        Map<Character, String[]> itemParamsMap = new HashMap<>();
        for (int colIndex = 0, craftingTableIngredientSlotsLength = CRAFTING_TABLE_INGREDIENT_SLOTS.length; colIndex < craftingTableIngredientSlotsLength; colIndex++) {
            int[] columnSlots = CRAFTING_TABLE_INGREDIENT_SLOTS[colIndex];

            int longestTextLength = 0;
            String[] rawColumnTexts = new String[3];

            for (int rowIndex = 0, columnSlotsLength = columnSlots.length; rowIndex < columnSlotsLength; rowIndex++) {
                int slot = columnSlots[rowIndex];

                ItemStack itemStack = items.get(slot);
                if (itemStack.isEmpty()) {
                    rawColumnTexts[rowIndex] = "";
                    continue;
                }

                String itemName = itemStack.getHoverName().getString();
                String text = itemName + (itemStack.getCount() > 1 ? ", " + itemStack.getCount() : "");
                rawColumnTexts[rowIndex] = text;

                longestTextLength = Math.max(longestTextLength, text.length());
            }

            String[] columnTexts = new String[3];
            for (int i = 0, rawColumnTextsLength = rawColumnTexts.length; i < rawColumnTextsLength; i++) {
                String text = rawColumnTexts[i];
                columnTexts[i] = text + " ".repeat(longestTextLength - text.length());
            }

            char letter = switch (colIndex) {
                case 0 -> 'A';
                case 1 -> 'B';
                case 2 -> 'C';
                default -> throw new IllegalStateException("Unexpected value: " + colIndex);
            };

            itemParamsMap.put(letter, columnTexts);
        }

        for (int rowIndex = 0; rowIndex <= 2; rowIndex++) {
            StringBuilder text = new StringBuilder();
            for (char key : new char[]{'A', 'B', 'C'}) {
                text.append(" |").append(key).append(rowIndex + 1).append(" = ").append(itemParamsMap.get(key)[rowIndex]);
            }
            builder.append("\n").append(text);
        }

        String outputText = "";
        if (product != null) {
            outputText = product.getHoverName().getString();
            outputText = outputText + (product.getCount() > 1 ? ", " + product.getCount() : "");
        }
        builder.append("\n |Output = ").append(outputText).append("\n }}\n}}");

        Minecraft.getInstance().gui.chatListener().handleSystemMessage(Component.translatable("message.wikitools.copied_recipe"), false);
        ClipboardHelper.setClipboard(builder.toString());
    }

    private void copyOpenedUiTemplateCall(Minecraft client, boolean fillWithBlankByDefault, boolean alwaysUseMcItemNameForNonSkullItems) {
        GetOpenedUiHandler.GetOpenedUiRequest request = new GetOpenedUiHandler.GetOpenedUiRequest(
                fillWithBlankByDefault,
                alwaysUseMcItemNameForNonSkullItems
        );
        String templateCall = getOpenedUiHandler.getOpenedUiTemplateCall(request);

        if (templateCall == null || templateCall.isEmpty()) {
            return;  // No ui to copy
        }
        ClipboardHelper.setClipboard(templateCall);

        Component tick = Component.literal("(✔)");
        Component cross = Component.literal("(✘)");
        MutableComponent fwbbdOptionTips = Component.literal("(◕‿◕)").setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent.ShowText(Component.translatable("message.wikitools.copy_opened_ui.fwbbd_mode_tip"))));
        MutableComponent auminfnsiOptionTips = Component.literal("(◕‿◕)").setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent.ShowText(Component.translatable("message.wikitools.copy_opened_ui.auminfnsi_mode_tip"))));
        MutableComponent outputText = Component.translatable("message.wikitools.copy_opened_ui.success").append("\n")
                .append("├ ").append(fillWithBlankByDefault ? tick : cross).append(" ")
                    .append(Component.translatable("message.wikitools.copy_opened_ui.fwbbd_mode_name")).append(" ")
                    .append(fwbbdOptionTips).append("\n")
                .append("└ ").append(alwaysUseMcItemNameForNonSkullItems ? tick : cross).append(" ")
                    .append(Component.translatable("message.wikitools.copy_opened_ui.auminfnsi_mode_name")).append(" ")
                    .append(auminfnsiOptionTips);
        client.gui.chatListener().handleSystemMessage(outputText, false);
    }
}
