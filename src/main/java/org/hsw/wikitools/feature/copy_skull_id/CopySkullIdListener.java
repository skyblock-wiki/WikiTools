package org.hsw.wikitools.feature.copy_skull_id;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.hsw.wikitools.common.ClipboardHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

import static org.hsw.wikitools.ModProperties.CATEGORY;

public class CopySkullIdListener {
    private final GetSkullIdHandler getSkullIdHandler;
    private final KeyMapping copySkullIdKeyBinding;

    public CopySkullIdListener(GetSkullIdHandler getSkullIdHandler) {
        this.getSkullIdHandler = getSkullIdHandler;

        this.copySkullIdKeyBinding = registerKeyBinding();

        registerEvent();
    }

     private KeyMapping registerKeyBinding() {
         return KeyMappingHelper.registerKeyMapping(new KeyMapping(
             "key.wikitools.copy_skull_id",
             InputConstants.Type.KEYSYM,
             GLFW.GLFW_KEY_Z,
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

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(Minecraft client) {
        while (client.screen == null && copySkullIdKeyBinding.consumeClick()) {
            copySkullId(client);
        }
    }

    private void onKeyPress(Minecraft client, KeyEvent keyInput) {
        if (copySkullIdKeyBinding.matches(keyInput)) {
            copySkullId(client);
        }
    }

    private void copySkullId(Minecraft client) {
        Optional<GetSkullIdHandler.GetSkullIdResponse> response = getSkullIdHandler.getSkullId(new GetSkullIdHandler.GetSkullIdRequest());
        if (response.isEmpty()) {
            return;
        }
        String stringToCopy = response.get().textureId;
        ClipboardHelper.setClipboard(stringToCopy);
        client.getChatListener().handleSystemMessage(Component.translatable("message.wikitools.copy_skull_id.success"), false);
    }
}
