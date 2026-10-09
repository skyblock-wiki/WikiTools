package org.hsw.wikitools.feature.copy_chat_message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.util.Mth;
import org.hsw.wikitools.mixin.copy_chat_message.ChatComponentAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageFinder {
    // mirrors the layout constants of ChatComponent#extractRenderState
    private static final int CHAT_X_OFFSET = 4;
    private static final int LINE_BACKGROUND_LEFT = -4;
    private static final int LINE_BACKGROUND_RIGHT_PADDING = 8;

    public List<VisibleChatLine> findVisibleChatLines() {
        Minecraft client = Minecraft.getInstance();
        ChatComponent chat = client.gui.hud.getChat();

        if (!chat.isChatFocused()) {
            return List.of();
        }

        ChatComponentAccessor chatAccessor = (ChatComponentAccessor) chat;
        List<GuiMessage.Line> trimmedMessages = chatAccessor.getTrimmedMessages();
        int scrollPos = chatAccessor.getChatScrollbarPos();
        int visibleLineCount = Math.min(trimmedMessages.size() - scrollPos, chat.getLinesPerPage());

        float scale = (float) chatAccessor.invokeGetScale();
        int chatWidth = Mth.ceil(chatAccessor.invokeGetWidth() / scale);
        int lineHeight = chatAccessor.invokeGetLineHeight();
        int chatBottom = Mth.floor((client.getWindow().getGuiScaledHeight() - ChatComponentAccessor.getBottomMargin()) / scale);

        int left = Mth.floor((LINE_BACKGROUND_LEFT + CHAT_X_OFFSET) * scale);
        int right = Mth.ceil((chatWidth + LINE_BACKGROUND_RIGHT_PADDING + CHAT_X_OFFSET) * scale);

        List<VisibleChatLine> visibleChatLines = new ArrayList<>();
        for (int lineIndex = 0; lineIndex < visibleLineCount; lineIndex++) {
            GuiMessage.Line line = trimmedMessages.get(lineIndex + scrollPos);
            int lineBottom = chatBottom - lineIndex * lineHeight;
            int lineTop = lineBottom - lineHeight;

            visibleChatLines.add(new VisibleChatLine(
                    line.parent(),
                    left,
                    Mth.floor(lineTop * scale),
                    right,
                    Mth.floor(lineBottom * scale)
            ));
        }
        return visibleChatLines;
    }

    @Nullable
    public GuiMessage findHoveredChatMessage(double mouseX, double mouseY) {
        for (VisibleChatLine visibleChatLine : findVisibleChatLines()) {
            if (visibleChatLine.contains(mouseX, mouseY)) {
                return visibleChatLine.message();
            }
        }
        return null;
    }

    /**
     * Find displayed chat messages from one message to another (both included), ordered from oldest to newest.
     */
    public List<GuiMessage> findChatMessagesBetween(GuiMessage firstMessage, GuiMessage secondMessage) {
        ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
        List<GuiMessage.Line> trimmedMessages = ((ChatComponentAccessor) chat).getTrimmedMessages();

        int firstIndex = indexOfMessage(trimmedMessages, firstMessage);
        int secondIndex = indexOfMessage(trimmedMessages, secondMessage);
        if (firstIndex == -1 || secondIndex == -1) {
            return List.of(); // if a message "disappears" from the chat during copy
        }

        // order from newest to oldest
        List<GuiMessage> messages = new ArrayList<>();
        for (int i = Math.max(firstIndex, secondIndex); i >= Math.min(firstIndex, secondIndex); i--) {
            GuiMessage message = trimmedMessages.get(i).parent();
            if (messages.isEmpty() || messages.getLast() != message) {
                messages.add(message);
            }
        }
        return messages;
    }

    private static int indexOfMessage(List<GuiMessage.Line> lines, GuiMessage message) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).parent() == message) {
                return i;
            }
        }
        return -1;
    }
}
