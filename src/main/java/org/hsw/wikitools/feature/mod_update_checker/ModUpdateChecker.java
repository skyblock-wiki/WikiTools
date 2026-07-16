package org.hsw.wikitools.feature.mod_update_checker;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonColors;
import org.hsw.wikitools.ModProperties;

import java.net.URI;


public class ModUpdateChecker {

    private final GetNewVersionHandler getNewVersionHandler;
    private boolean ranOnceAfterClientLaunch = false;

    public ModUpdateChecker(GetNewVersionHandler getNewVersionHandler) {
        this.getNewVersionHandler = getNewVersionHandler;

        registerEvent();
    }

    private void registerEvent() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            var isSelfJoin = client.player == Minecraft.getInstance().player;
            var isSinglePlayer = client.isSingleplayer();

            if (!isSelfJoin || isSinglePlayer) {
                return;
            }

            handleModUpdateCheck();
        });
    }

    private void handleModUpdateCheck() {
        if (ranOnceAfterClientLaunch) {
            return;
        }
        ranOnceAfterClientLaunch = true;

        String currentVersionName = ModProperties.MOD_VERSION;
        getNewVersionHandler.getNewVersion(
                new GetNewVersionHandler.GetNewVersionRequest(currentVersionName))
                .thenAccept((response) -> {
                    if (!response.success || response.result.isEmpty()) {
                        warnFailure(response.message.orElse("Unknown error"));
                        return;
                    }

                    if (response.result.get().hasNewRelease) {
                        remindUserToUpdateMod(response.result.get().latestVersion);
                    }
                });
    }

    private static void remindUserToUpdateMod(String newVersionName) {
        Component frontComponent = Component.translatable("message.wikitools.mod_update_checker.new_update", newVersionName)
                .setStyle(Style.EMPTY.withColor(CommonColors.GREEN));

        String latestReleaseDownloadUrl = ModProperties.LATEST_RELEASE_DOWNLOAD_URL;
        Style linkStyle = Style.EMPTY
                .withColor(CommonColors.GRAY)
                .withUnderlined(true)
                .withClickEvent(new ClickEvent.OpenUrl(URI.create(latestReleaseDownloadUrl)));

        Component linkComponent = Component.translatable("message.wikitools.mod_update_checker.update_link_text")
                .setStyle(linkStyle);

        Component messageComponent = Component.literal("")
                .append(frontComponent)
                .append(" ")
                .append(linkComponent);

        printMessageInMainThread(messageComponent);
    }

    private static void printMessageInMainThread(Component message) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.gui.getChat().addClientSystemMessage(message));
    }

    private static void warnFailure(String problemName) {
        String warningText = Component.translatable("message.wikitools.mod_update_checker.error", problemName).getString();
        ModProperties.LOGGER.warn(warningText, false);
    }

}
