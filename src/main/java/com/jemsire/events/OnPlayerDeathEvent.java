package com.jemsire.events;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.jemsire.config.DeathConfig;
import com.jemsire.plugin.JemDeaths;
import com.jemsire.utils.ChatBroadcaster;
import com.jemsire.utils.PlaceholderReplacer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class OnPlayerDeathEvent extends DeathSystems.OnDeathSystem {
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    public void onComponentAdded(@Nonnull Ref ref, @Nonnull DeathComponent component, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) {
        Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
        DeathComponent deathComponent = (DeathComponent)store.getComponent(ref, DeathComponent.getComponentType());
        
        if (playerComponent != null && deathComponent != null && playerRef != null) {
            JemDeaths plugin = JemDeaths.get();
            if (plugin == null) {
                return;
            }
            
            DeathConfig deathConfig = plugin.getDeathConfig().get();
            if (deathConfig == null) {
                return;
            }
            
            String playerName = playerComponent.getDisplayName();
            String rawDeathCause = deathComponent.getDeathMessage().getAnsiMessage();
            String deathCause = rawDeathCause.replace("You were", deathConfig.getDeathCauseReplacement());
            
            // Get player position
            String positionString = "Unknown";
            double x = 0, y = 0, z = 0;

            try {
                Transform transform = playerRef.getTransform();
                x = transform.getPosition().x;
                y = transform.getPosition().y;
                z = transform.getPosition().z;
                positionString = String.format("%.1f, %.1f, %.1f", x, y, z);
            } catch (Exception e) {
                // Position not available, use default
            }
            
            // Prepare placeholders for replacement
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", playerName);
            placeholders.put("playerName", playerName);
            placeholders.put("deathCause", deathCause);
            placeholders.put("rawDeathCause", rawDeathCause);
            placeholders.put("position", positionString);
            placeholders.put("x", String.format("%.1f", x));
            placeholders.put("y", String.format("%.1f", y));
            placeholders.put("z", String.format("%.1f", z));
            
            // Send death message to everyone if enabled
            if (deathConfig.isShowDeathMessage()) {
                // Format death announcement message using config
                String deathMessage = PlaceholderReplacer.replacePlaceholders(
                    deathConfig.getDeathAnnouncementFormat(), 
                    placeholders
                );
                // Broadcast to all players
                ChatBroadcaster.broadcastToAll(deathMessage);
            }
            
            // Send position notification to the player if enabled
            if (deathConfig.isShowPosition()) {
                // Format position message using config
                String positionMessage = PlaceholderReplacer.replacePlaceholders(
                    deathConfig.getDeathLocationFormat(),
                    placeholders
                );
                ChatBroadcaster.sendToPlayer(playerRef, positionMessage);
            }
        }
    }
}
