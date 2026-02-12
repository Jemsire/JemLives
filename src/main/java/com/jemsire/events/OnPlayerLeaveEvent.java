package com.jemsire.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.jemsire.plugin.JemLives;
import com.jemsire.utils.Logger;

/**
 * Handles player disconnect to clean up UI and other resources.
 */
public class OnPlayerLeaveEvent {

    public static void onPlayerLeave(PlayerDisconnectEvent event) {
        JemLives plugin = JemLives.get();
        if (plugin == null) return;

        Logger.debug("Player " + event.getPlayerRef().getUuid() + " disconnecting, cleaning up lives HUD.");
        // We don't have Ref<EntityStore> here easily, but we have UUID.
        // Let's modify LivesHudManager to allow removal by UUID or just clean up the map.
        plugin.getLivesHudManager().removeByUuid(event.getPlayerRef().getUuid());
    }
}
