package org.hsw.wikitools.feature.copy_item_tooltip;

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

public class CopyHoveredItemTooltipListener {
    private final GetItemTooltipHandler getItemTooltipHandler;
    private final KeyMapping copyTooltipKeyBinding;

    public CopyHoveredItemTooltipListener(GetItemTooltipHandler getItemTooltipHandler) {
        this.getItemTooltipHandler = getItemTooltipHandler;

        this.copyTooltipKeyBinding = registerKeyBinding();

        registerEvent();
    }

     private KeyMapping registerKeyBinding() {
         return KeyMappingHelper.registerKeyMapping(new KeyMapping(
             "key.wikitools.copy_tooltip",
             InputConstants.Type.KEYSYM,
             GLFW.GLFW_KEY_X,
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
        boolean xIsPressed = copyTooltipKeyBinding.matches(keyInput);
        boolean xIsPressedWithShift = xIsPressed && (keyInput.hasShiftDown());

        if (xIsPressed && xIsPressedWithShift) {
            copyTooltipAsModuleData(client);
        }
        else if (xIsPressed) {
            copyTooltipAsTemplateCall(client);
        }
    }

    private void copyTooltipAsTemplateCall(Minecraft client) {
        Optional<GetItemTooltipHandler.GetItemTooltipResponse> tooltip =
                getItemTooltipHandler.getInventorySlotTemplateCall(new GetItemTooltipHandler.GetItemTooltipRequest());
        if (tooltip.isEmpty()) {
            return; // No tooltip to copy
        }
        String stringToCopy = tooltip.get().tooltip;
        ClipboardHelper.setClipboard(stringToCopy);

        MutableComponent formattingModeTip = Component.literal("(◕‿◕)").setStyle(
                Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.translatable(
                        "message.wikitools.copy_tooltip.formatting_mode_tip"))));
        MutableComponent outputText = Component.translatable("message.wikitools.copy_tooltip.success").append("\n")
                .append("└ ").append(Component.translatable("message.wikitools.copy_tooltip.with_template_formatting")).append(" ").append(formattingModeTip);
        client.getChatListener().handleSystemMessage(Component.translationArg(outputText), false);
    }

    private void copyTooltipAsModuleData(Minecraft client) {
        Optional<GetItemTooltipHandler.GetItemTooltipResponse> tooltip =
                getItemTooltipHandler.getTooltipModuleDataItem(new GetItemTooltipHandler.GetItemTooltipRequest());

        if (tooltip.isEmpty()) {
            return; // No tooltip to copy
        }
        String stringToCopy = tooltip.get().tooltip;
        ClipboardHelper.setClipboard(stringToCopy);

        MutableComponent formattingModeTip = Component.literal("(◕‿◕)").setStyle(
                Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.translatable(
                        "message.wikitools.copy_tooltip.formatting_mode_tip"))));
        MutableComponent outputText = Component.translatable("message.wikitools.copy_tooltip.success").append("\n")
                .append("└ ").append(Component.translatable("message.wikitools.copy_tooltip.with_module_formatting")).append(" ").append(formattingModeTip);
        client.getChatListener().handleSystemMessage(Component.translationArg(outputText), false);
    }

}
