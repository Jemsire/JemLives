package com.jemsire.events;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.jemsire.config.LivesConfig;
import com.jemsire.plugin.JemLives;
import com.jemsire.utils.ChatBroadcaster;
import com.jemsire.utils.LivesManager;
import com.jemsire.utils.Logger;
import com.jemsire.utils.PlaceholderReplacer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnPlayerDeathEvent extends DeathSystems.OnDeathSystem {
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType(), DeathComponent.getComponentType());
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent deathComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        
        if (playerComponent != null && playerRef != null) {
            String playerName = playerComponent.getDisplayName();
            UUID playerUuid = playerRef.getUuid();

            Logger.debug("Handling death for player: " + playerName + " (" + playerUuid + ")");

            JemLives plugin = JemLives.get();
            if (plugin == null) {
                return;
            }
            
            LivesConfig livesConfig = plugin.getLivesConfig().get();
            LivesManager livesManager = plugin.getLivesManager();
            if (livesConfig == null || livesManager == null) {
                return;
            }
            
            // Handle lives loss
            boolean isPvp = false; 
            boolean shouldLoseLife = !livesConfig.isLoseLivesFromPvpOnly(); 
            
            if (shouldLoseLife) {
                livesManager.removeLife(playerUuid);
            }

            int currentLives = livesManager.getLives(playerUuid);
            
            String rawDeathCause = deathComponent.getDeathMessage().getAnsiMessage();
            String deathCause = rawDeathCause.replace("You were", livesConfig.getDeathCauseReplacement());
            
            // Prepare placeholders for replacement
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", playerName);
            placeholders.put("playerName", playerName);
            placeholders.put("deathCause", deathCause);
            placeholders.put("rawDeathCause", rawDeathCause);
            placeholders.put("lives", String.valueOf(currentLives));
            
            // Global death message
            String deathAnnouncement = PlaceholderReplacer.replacePlaceholders(
                livesConfig.getDeathAnnouncementFormat(), 
                placeholders
            );
            ChatBroadcaster.broadcastToAll(deathAnnouncement);
            
            // Local death message
            String localMessage = PlaceholderReplacer.replacePlaceholders(
                livesConfig.getLocalDeathMessage(),
                placeholders
            );
            ChatBroadcaster.sendToPlayer(playerRef, localMessage);

            // Update lives HUD
            plugin.updateLivesHud(playerUuid, currentLives);

            // Action if 0 lives
            if (currentLives <= 0) {
                long remainingSeconds = livesManager.getRemainingRegenTime(playerUuid);
                String timeString = formatTime(remainingSeconds);
                
                placeholders.put("time", timeString);
                String kickMsg = PlaceholderReplacer.replacePlaceholders(livesConfig.getKickMessage(), placeholders);
                
                if ("KICK".equalsIgnoreCase(livesConfig.getZeroLivesAction())) {
                    plugin.getKickManager().queueKick(playerRef, kickMsg);
                }
                
                Logger.info("Player " + playerName + " has 0 lives. Message: " + kickMsg);
            }
        }
    }

    private String formatTime(long seconds) {
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
