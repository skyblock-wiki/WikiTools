package org.hsw.wikitools.common;

import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

public class ClipboardHelper {
    public static void setClipboard(String text) {
        Window window = Minecraft.getInstance().getWindow();
        ClipboardManager clipboard = new ClipboardManager();
        clipboard.setClipboard(window, text);
    }

    public static String getClipboard() {
        Window window = Minecraft.getInstance().getWindow();
        ClipboardManager clipboard = new ClipboardManager();

        return clipboard.getClipboard(window, (ret, args) -> {});
    }
}
