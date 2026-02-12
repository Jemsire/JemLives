package com.jemsire.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.jemsire.config.LivesConfig;
import com.jemsire.ui.LivesHud;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the lives HUD for each player using native Hytale CustomUIHud.
 * Shows an always-visible HUD with the lives icon and count (see JemLives/lives_hud.ui).
 */
public class LivesHudManager {
    private final Config<LivesConfig> livesConfig;
    private final Map<UUID, LivesHud> activeHuds = new HashMap<>();

    public LivesHudManager(Config<LivesConfig> livesConfig) {
        this.livesConfig = livesConfig;
    }

    /** Show the lives HUD to a player. Call when the player is ready (e.g. PlayerReadyEvent). */
    public void show(Ref<EntityStore> ref, Store<EntityStore> store, int lives) {
        if (livesConfig == null || !livesConfig.get().isShowLivesHud()) return;

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        UUID uuid = playerRef.getUuid();
        
        // If HUD already exists in mapping, we should still ensure it's set on the client
        // This handles cases where client-side HUD might have been lost but server mapping remained.
        HudManager hudManager = player.getHudManager();
        
        LivesHud livesHud;
        if (activeHuds.containsKey(uuid)) {
            livesHud = activeHuds.get(uuid);
            Logger.debug("HUD already exists in mapping for " + playerRef.getUsername() + ", reapplying to client.");
        } else {
            livesHud = new LivesHud(playerRef);
            activeHuds.put(uuid, livesHud);
        }
        
        hudManager.setCustomHud(playerRef, livesHud);
        livesHud.refresh();
    }

    /** Update the lives count on the HUD (e.g. after death or regen). */
    public void update(UUID uuid, int lives) {
        LivesHud hud = activeHuds.get(uuid);
        if (hud != null) {
            hud.refresh();
        }
    }

    /** Remove the lives HUD from a player (e.g. on disconnect). */
    public void remove(Ref<EntityStore> ref, Store<EntityStore> store) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        HudManager hudManager = player.getHudManager();
        hudManager.setCustomHud(playerRef, null);
        activeHuds.remove(playerRef.getUuid());
    }

    /** Remove the lives HUD mapping by UUID (for cleanup). */
    public void removeByUuid(UUID uuid) {
        activeHuds.remove(uuid);
    }
}
