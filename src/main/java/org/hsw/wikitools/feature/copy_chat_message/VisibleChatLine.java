package org.hsw.wikitools.feature.copy_chat_message;

import net.minecraft.client.multiplayer.chat.GuiMessage;

public record VisibleChatLine(GuiMessage message, int left, int top, int right, int bottom) {
    public boolean contains(double x, double y) {
        return x >= left && x < right && y >= top && y < bottom;
    }
}
