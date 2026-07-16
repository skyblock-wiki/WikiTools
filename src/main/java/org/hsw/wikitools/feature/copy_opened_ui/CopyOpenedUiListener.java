package org.hsw.wikitools.feature.copy_opened_ui;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.hsw.wikitools.common.ClipboardHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

import static org.hsw.wikitools.ModProperties.CATEGORY;

public class CopyOpenedUiListener {
    private final GetOpenedUiHandler getOpenedUiHandler;
    private final KeyMapping copyOpenedUiKeybinding;

    public CopyOpenedUiListener(GetOpenedUiHandler getOpenedUiHandler) {
        this.getOpenedUiHandler = getOpenedUiHandler;

        this.copyOpenedUiKeybinding = registerKeyBinding();

        registerEvent();
    }

    private KeyMapping registerKeyBinding() {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.wikitools.copy_opened_ui",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                CATEGORY
        ));
    }

    private void registerEvent() {
        // Add a listener for screen events (when a screen is opened)
        // to add a keyboard event listener for the HandledScreen

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            // Add a listener for keyboard events if the screen is a HandledScreen

            if (!(screen instanceof AbstractContainerScreen<?>)) {
                return; // Only register for InventoryScreen
            }

            ScreenKeyboardEvents.afterKeyPress(screen).register((screen1, keyInput) -> onKeyPress(client, keyInput));
        });
    }

    private void onKeyPress(Minecraft client, KeyEvent keyInput) {
        boolean cIsPressed = copyOpenedUiKeybinding.matches(keyInput);
        boolean cIsPressedWithShift = cIsPressed && (keyInput.hasShiftDown());
        boolean cIsPressedWithControl = cIsPressed && (keyInput.hasControlDown());

        if (cIsPressed) {
            copyOpenedUiTemplateCall(client, cIsPressedWithShift, cIsPressedWithControl);
        }
    }

    private void copyOpenedUiTemplateCall(Minecraft client, boolean fillWithBlankByDefault, boolean alwaysUseMcItemNameForNonSkullItems) {
        GetOpenedUiHandler.GetOpenedUiRequest request = new GetOpenedUiHandler.GetOpenedUiRequest(
                fillWithBlankByDefault,
                alwaysUseMcItemNameForNonSkullItems
        );
        Optional<GetOpenedUiHandler.GetOpenedUiResponse> response = getOpenedUiHandler.getOpenedUiTemplateCall(request);

        if (response.isEmpty()) {
            return;  // No ui to copy
        }
        String stringToCopy = response.get().templateCall;
        ClipboardHelper.setClipboard(stringToCopy);

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
        client.getChatListener().handleSystemMessage(outputText, false);
    }
}
