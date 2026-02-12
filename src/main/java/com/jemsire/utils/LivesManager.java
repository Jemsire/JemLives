package com.jemsire.utils;

import com.jemsire.config.LivesConfig;
import com.jemsire.config.PlayerData;
import com.jemsire.plugin.JemLives;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class LivesManager {
    private final Random random = new Random();
    private final Map<UUID, PlayerData> cachedData = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File playersFolder;

    public LivesManager() {
        Path path = JemLives.get().getDataDirectory();
        this.playersFolder = new File(path.toFile(), "players");
        if (!this.playersFolder.exists()) {
            this.playersFolder.mkdirs();
        }
    }

    private int generateInitialLives() {
        LivesConfig config = JemLives.get().getLivesConfig().get();
        int min = config.getInitialLivesMin();
        int max = config.getInitialLivesMax();
        if (min >= max) return min;
        return random.nextInt(max - min + 1) + min;
    }

    private PlayerData getPlayerData(UUID uuid) {
        if (cachedData.containsKey(uuid)) {
            return cachedData.get(uuid);
        }

        File playerFile = new File(playersFolder, uuid.toString() + ".json");
        PlayerData data;

        if (playerFile.exists()) {
            try (FileReader reader = new FileReader(playerFile)) {
                data = gson.fromJson(reader, PlayerData.class);
            } catch (IOException e) {
                Logger.severe("Failed to load player data for " + uuid, e);
                data = new PlayerData();
            }
        } else {
            data = new PlayerData();
            savePlayerData(uuid, data);
        }

        cachedData.put(uuid, data);
        return data;
    }

    private void savePlayerData(UUID uuid, PlayerData data) {
        File playerFile = new File(playersFolder, uuid.toString() + ".json");
        try (FileWriter writer = new FileWriter(playerFile)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            Logger.severe("Failed to save player data for " + uuid, e);
        }
    }

    public int getLives(UUID uuid) {
        PlayerData data = getPlayerData(uuid);
        
        if (data.getLives() == 0 && data.getLastDeathTime() == 0) {
            int lives = generateInitialLives();
            data.setLives(lives);
            savePlayerData(uuid, data);
            return lives;
        }

        checkRegen(uuid);
        return data.getLives();
    }

    public void setLives(UUID uuid, int lives) {
        PlayerData data = getPlayerData(uuid);
        data.setLives(lives);
        savePlayerData(uuid, data);
    }

    public void removeLife(UUID uuid) {
        PlayerData data = getPlayerData(uuid);

        if (data.getLives() == 0 && data.getLastDeathTime() == 0) {
            data.setLives(generateInitialLives());
        }

        data.setLives(data.getLives() - 1);
        Logger.debug("Removed life from " + uuid + ". New lives count: " + data.getLives());
        if (data.getLives() <= 0) {
            data.setLives(0);
            data.setLastDeathTime(Instant.now().getEpochSecond());
        }
        savePlayerData(uuid, data);
    }

    public void addLife(UUID uuid) {
        PlayerData data = getPlayerData(uuid);
        
        if (data.getLives() == 0 && data.getLastDeathTime() == 0) {
            data.setLives(generateInitialLives());
        }

        data.setLives(data.getLives() + 1);
        Logger.debug("Added life to " + uuid + ". New lives count: " + data.getLives());
        savePlayerData(uuid, data);
    }

    public long getRemainingRegenTime(UUID uuid) {
        PlayerData data = getPlayerData(uuid);

        if (data.getLives() > 0) return 0;
        if (data.getLastDeathTime() == 0) return 0;

        LivesConfig livesConfig = JemLives.get().getLivesConfig().get();
        long now = Instant.now().getEpochSecond();
        long diff = now - data.getLastDeathTime();
        long regenTimeSeconds = livesConfig.getRegenTimeMinutes() * 60L;

        return Math.max(0, regenTimeSeconds - diff);
    }

    private void checkRegen(UUID uuid) {
        PlayerData data = getPlayerData(uuid);

        if (data.getLives() > 0) return;
        if (data.getLastDeathTime() == 0) return;

        LivesConfig livesConfig = JemLives.get().getLivesConfig().get();
        long now = Instant.now().getEpochSecond();
        long diff = now - data.getLastDeathTime();
        long regenTimeSeconds = livesConfig.getRegenTimeMinutes() * 60L;

        if (diff >= regenTimeSeconds) {
            int newLives = generateInitialLives();
            data.setLives(newLives);
            data.setLastDeathTime(0);
            Logger.debug("Player " + uuid + " has regenerated " + newLives + " lives.");
            savePlayerData(uuid, data);
        }
    }
}
