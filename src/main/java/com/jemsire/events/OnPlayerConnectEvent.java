package com.jemsire.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
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
 * Handles player join to ensure player data is created early and sends join message.
 */
public class OnPlayerConnectEvent {
    
    public static void onPlayerConnect(PlayerConnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        if (playerRef == null) return;

        Logger.debug("Player " + playerRef.getUsername() + " connecting (UUID: " + playerRef.getUuid() + ")");

        JemLives plugin = JemLives.get();
        if (plugin == null) return;

        LivesManager livesManager = plugin.getLivesManager();
        
        if (livesManager != null) {
            UUID playerUuid = playerRef.getUuid();
            
            // This will create the player config file if it doesn't exist
            // and trigger regeneration if time has passed.
            livesManager.getLives(playerUuid);
        }
    }

}
