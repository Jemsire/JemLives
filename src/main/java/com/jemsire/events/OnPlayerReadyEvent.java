package com.jemsire.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.jemsire.config.LivesConfig;
import com.jemsire.plugin.JemLives;
import com.jemsire.utils.ChatBroadcaster;
import com.jemsire.utils.LivesManager;
import com.jemsire.utils.Logger;
import com.jemsire.utils.PlaceholderReplacer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the PlayerReadyEvent to send join notifications when the player is fully ready.
 */
public class OnPlayerReadyEvent {

    public static void onPlayerReady(PlayerReadyEvent event) {
        Ref<EntityStore> ref = event.getPlayerRef();

        JemLives plugin = JemLives.get();
        if (plugin == null) return;

        PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        Logger.debug("Player " + playerRef.getUsername() + " is ready.");

        LivesManager livesManager = plugin.getLivesManager();
        LivesConfig livesConfig = plugin.getLivesConfig().get();

        if (livesManager != null && livesConfig != null) {
            UUID playerUuid = playerRef.getUuid();
            int currentLives = livesManager.getLives(playerUuid);

            // Notify player about their lives when they are fully ready in the world
            if (currentLives > 0) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("lives", String.valueOf(currentLives));
                placeholders.put("player", playerRef.getUsername());

                String joinMessage = PlaceholderReplacer.replacePlaceholders(livesConfig.getLivesCommandMessage(), placeholders);
                ChatBroadcaster.sendToPlayer(playerRef, joinMessage);
            }

            // Show lives HUD near hotbar when player is ready (use world store/ref; player is entity in world)
            if (livesConfig.isShowLivesHud()) {
                plugin.showLivesHud(ref, ref.getStore(), currentLives);
            }

            // Handle kick if 0 lives
            if (currentLives <= 0) {
                long remainingSeconds = livesManager.getRemainingRegenTime(playerUuid);
                if (remainingSeconds > 0) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("lives", String.valueOf(currentLives));
                    placeholders.put("player", playerRef.getUsername());
                    placeholders.put("time", formatTime(remainingSeconds));

                    String kickMsg = PlaceholderReplacer.replacePlaceholders(livesConfig.getKickMessage(), placeholders);

                    if ("KICK".equalsIgnoreCase(livesConfig.getZeroLivesAction())) {
                        Logger.info("Queuing kick for player " + playerRef.getUsername() + " due to 0 lives.");
                        plugin.getKickManager().queueKick(playerRef, kickMsg);
                    }
                }
            }
        }
    }

    private static String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(secs).append("s");
        return sb.toString();
    }
}
