package org.hsw.wikitools.feature.copy_chat_message;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.hsw.wikitools.common.ClipboardHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.hsw.wikitools.ModProperties.LOGGER;

public class CopyChatMessageListener {
    private static final int HOVER_HIGHLIGHT_COLOR = 0x30FFFFFF;
    private static final int SELECTION_HIGHLIGHT_COLOR = 0x50FFAA00;
    private static final int SELECTION_EDGE_COLOR = 0xFFFFAA00;

    private final GetChatMessageHandler getChatMessageHandler;
    private final ChatMessageFinder chatMessageFinder;

    @Nullable
    private GuiMessage selectionStart;

    public CopyChatMessageListener(GetChatMessageHandler getChatMessageHandler, ChatMessageFinder chatMessageFinder) {
        this.getChatMessageHandler = getChatMessageHandler;
        this.chatMessageFinder = chatMessageFinder;
    }

    public void registerEvent() {
        ScreenEvents.BEFORE_INIT.register((client, screen, _, _) -> {
            if (screen instanceof ChatScreen) {
                ScreenMouseEvents.allowMouseClick(screen).register((_, mouseEvent) -> onMouseClick(client, mouseEvent));
                ScreenEvents.afterExtract(screen).register((_, graphics, mouseX, mouseY, _) -> highlightChatMessages(client, graphics, mouseX, mouseY));
                ScreenEvents.remove(screen).register(_ -> selectionStart = null);
            }
        });
    }

    private boolean onMouseClick(Minecraft client, MouseButtonEvent mouseEvent) {
        boolean isControlClick = mouseEvent.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && mouseEvent.hasControlDown();
        if (!isControlClick) {
            return true;
        }

        GuiMessage hoveredMessage = chatMessageFinder.findHoveredChatMessage(mouseEvent.x(), mouseEvent.y());
        if (hoveredMessage == null) {
            return true; // let the chat screen handle the click
        }

        if (mouseEvent.hasShiftDown()) {
            selectChatMessage(client, hoveredMessage);
        }
        else {
            copyChatMessage(client, hoveredMessage);
        }
        return false; // stop click event propagation (avoid cmd being triggered)
    }

    private void copyChatMessage(Minecraft client, GuiMessage message) {
        selectionStart = null;

        LegacyText chatMessage = getChatMessageHandler.getChatMessage(message);
        ClipboardHelper.setClipboard(chatMessage.text());

        MutableComponent outputText = Component.translatable("message.wikitools.copy_chat_message.success");
        client.gui.chatListener().handleSystemMessage(
                appendUnknownColorsError(outputText, chatMessage.unknownColors()), false);
    }

    private void selectChatMessage(Minecraft client, GuiMessage message) {
        if (selectionStart == null) {
            selectionStart = message;
            return;
        }

        List<LegacyText> chatMessages = getChatMessageHandler.getChatMessagesBetween(selectionStart, message);
        selectionStart = null;
        if (chatMessages.isEmpty()) {
            return;
        }

        ClipboardHelper.setClipboard(String.join("\n", chatMessages.stream().map(LegacyText::text).toList()));

        Set<String> unknownColors = new LinkedHashSet<>();
        chatMessages.forEach(chatMessage -> unknownColors.addAll(chatMessage.unknownColors()));

        MutableComponent outputText = Component.translatable(
                "message.wikitools.copy_chat_message.success_selection", chatMessages.size());
        client.gui.chatListener().handleSystemMessage(appendUnknownColorsError(outputText, unknownColors), false);
    }

    private static MutableComponent appendUnknownColorsError(MutableComponent outputText, Set<String> unknownColors) {
        if (unknownColors.isEmpty()) {
            return outputText;
        }

        String colors = String.join(", ", unknownColors);
        LOGGER.error("[CopyChatMessage] Unknown colors ignored, they have no legacy formatting code: {}", colors);

        return outputText.append("\n").append("└ ").append(Component
                .translatable("message.wikitools.copy_chat_message.unknown_colors", colors)
                .withStyle(ChatFormatting.RED));
    }

    private void highlightChatMessages(Minecraft client, GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        GuiMessage hoveredMessage = chatMessageFinder.findHoveredChatMessage(mouseX, mouseY);
        boolean isSelecting = selectionStart != null;

        List<GuiMessage> highlightedMessages;
        if (isSelecting && hoveredMessage != null && client.hasControlDown() && client.hasShiftDown()) {
            highlightedMessages = chatMessageFinder.findChatMessagesBetween(selectionStart, hoveredMessage);
        }
        else if (isSelecting) {
            highlightedMessages = List.of(selectionStart);
        }
        else if (hoveredMessage != null && client.hasControlDown()) {
            highlightedMessages = List.of(hoveredMessage);
        }
        else {
            return;
        }

        Set<GuiMessage> highlightedMessageSet = Collections.newSetFromMap(new IdentityHashMap<>());
        highlightedMessageSet.addAll(highlightedMessages);

        for (VisibleChatLine line : chatMessageFinder.findVisibleChatLines()) {
            if (!highlightedMessageSet.contains(line.message())) {
                continue;
            }

            if (isSelecting) {
                graphics.fill(line.left(), line.top(), line.right(), line.bottom(), SELECTION_HIGHLIGHT_COLOR);
                graphics.fill(line.left(), line.top(), line.left() + 1, line.bottom(), SELECTION_EDGE_COLOR);
            }
            else {
                graphics.fill(line.left(), line.top(), line.right(), line.bottom(), HOVER_HIGHLIGHT_COLOR);
            }
        }
    }
}
