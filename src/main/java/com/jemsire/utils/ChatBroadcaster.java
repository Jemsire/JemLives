package com.jemsire.utils;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.jemsire.plugin.JemDeaths;

import java.awt.*;

/**
 * Utility class for broadcasting messages to players in chat.
 * Supports both plain text and color formatting (tags and legacy codes).
 */
public class ChatBroadcaster {
    
    /**
     * Broadcasts a message to all online players.
     * @param message The message to broadcast
     */
    public static void broadcastToAll(String message) {
        try {
            JemDeaths plugin = JemDeaths.get();
            if (plugin == null) {
                Logger.warning("Plugin instance not available for broadcasting");
                return;
            }
            
            Universe universe = Universe.get();
            if (universe == null) {
                Logger.warning("Universe not available for broadcasting");
                return;
            }
            
            // Format message with colors if tags/codes are present, otherwise use plain text
            Message msg = (MessageFormatter.containsColorTags(message) || message.contains("&"))
                ? MessageFormatter.format(message)
                : Message.raw(message);
            
            universe.getPlayers().forEach(playerRef -> {
                try {
                    playerRef.sendMessage(msg);
                } catch (Exception e) {
                    Logger.warning("Failed to send message to player: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Logger.severe("Failed to broadcast message", e);
        }
    }
    
    /**
     * Sends a message to a specific player.
     * @param playerRef The player reference
     * @param message The message to send
     */
    public static void sendToPlayer(PlayerRef playerRef, String message) {
        try {
            if (playerRef == null) {
                Logger.warning("PlayerRef is null, cannot send message");
                return;
            }
            
            // Format message with colors if tags/codes are present, otherwise use plain text
            Message msg = (MessageFormatter.containsColorTags(message) || message.contains("&"))
                ? MessageFormatter.format(message)
                : Message.raw(message);
            
            playerRef.sendMessage(msg);
        } catch (Exception e) {
            Logger.severe("Failed to send message to player", e);
        }
    }
}
