package org.hsw.wikitools.mixin.copy_chat_message;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {
    @Accessor("trimmedMessages")
    List<GuiMessage.Line> getTrimmedMessages();

    @Accessor("chatScrollbarPos")
    int getChatScrollbarPos();

    @Invoker("getScale")
    double invokeGetScale();

    @Invoker("getWidth")
    int invokeGetWidth();

    @Invoker("getLineHeight")
    int invokeGetLineHeight();
}
