package org.hsw.wikitools.feature.copy_chat_message;

import net.minecraft.client.multiplayer.chat.GuiMessage;

import java.util.List;

public class GetChatMessageHandler {
    private final ChatMessageFinder chatMessageFinder;
    private final LegacyTextFormatter legacyTextFormatter = new LegacyTextFormatter();

    public GetChatMessageHandler(ChatMessageFinder chatMessageFinder) {
        this.chatMessageFinder = chatMessageFinder;
    }

    public LegacyText getChatMessage(GuiMessage message) {
        return legacyTextFormatter.formatComponent(message.content());
    }

    public List<LegacyText> getChatMessagesBetween(GuiMessage firstMessage, GuiMessage secondMessage) {
        return chatMessageFinder.findChatMessagesBetween(firstMessage, secondMessage).stream()
                .map(this::getChatMessage)
                .toList();
    }
}
