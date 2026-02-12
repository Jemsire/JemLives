package com.jemsire.utils;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.jemsire.plugin.JemLives;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KickManager {
    private final Map<UUID, KickEntry> kickQueue = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public KickManager() {
        scheduler.scheduleAtFixedRate(this::tick, 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    public void queueKick(PlayerRef playerRef, String reason) {
        if (playerRef == null) return;
        UUID uuid = playerRef.getUuid();
        
        // Rework logic: set player lives to 0 if they are being queued for kick
        JemLives plugin = JemLives.get();
        if (plugin != null && plugin.getLivesManager() != null) {
            plugin.getLivesManager().setLives(uuid, 0);
        }

        if (!kickQueue.containsKey(uuid)) {
            Logger.debug("Queuing kick for player: " + playerRef.getUsername() + " (" + uuid + ") in 1 second.");
            kickQueue.put(uuid, new KickEntry(playerRef, uuid, reason, System.currentTimeMillis() + 1000));
        }
    }

    public void tick() {
        if (kickQueue.isEmpty()) return;

        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, KickEntry>> iterator = kickQueue.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, KickEntry> entry = iterator.next();
            KickEntry kickEntry = entry.getValue();

            if (now >= kickEntry.kickTime) {
                iterator.remove();
                try {
                    Logger.info("Executing delayed kick for: " + kickEntry.playerRef.getUsername());
                    kickEntry.playerRef.getPacketHandler().disconnect(kickEntry.reason);
                } catch (Exception e) {
                    // Player may have already disconnected; ref can be invalid. Do not spam console.
                    Logger.debug("Skipping kick for " + kickEntry.uuid + ": " + e.getMessage());
                }
            }
        }
    }

    private static class KickEntry {
        final PlayerRef playerRef;
        final UUID uuid;
        final String reason;
        final long kickTime;

        KickEntry(PlayerRef playerRef, UUID uuid, String reason, long kickTime) {
            this.playerRef = playerRef;
            this.uuid = uuid;
            this.reason = reason;
            this.kickTime = kickTime;
        }
    }
}
