package com.jemsire.plugin;

import com.hypixel.hytale.common.semver.Semver;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.jemsire.commands.LivesCommand;
import com.jemsire.config.LivesConfig;
import com.jemsire.events.OnPlayerConnectEvent;
import com.jemsire.events.OnPlayerDeathEvent;
import com.jemsire.events.OnPlayerLeaveEvent;
import com.jemsire.events.OnPlayerReadyEvent;
import com.jemsire.expansion.JemLivesExpansion;
import com.jemsire.jemplaceholders.api.JemPlaceholdersAPI;
import com.jemsire.utils.*;

import javax.annotation.Nonnull;
import java.util.UUID;

public class JemLives extends JavaPlugin {
    private static JemLives instance;

    private final Semver version;

    public static JemLives get() {
        return instance;
    }

    private final Config<LivesConfig> livesConfig;
    private final LivesManager livesManager;
    private final LivesHudManager livesHudManager;
    private final KickManager kickManager;

    public JemLives(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        Logger.info("Starting JemLives Plugin...");

        version = init.getPluginManifest().getVersion();

        this.livesConfig = this.withConfig("LivesConfig", LivesConfig.CODEC);
        this.livesManager = new LivesManager();
        this.livesHudManager = new LivesHudManager(this.livesConfig);
        this.kickManager = new KickManager();
    }

    @Override
    protected void setup() {
        // Register commands
        registerCommands();

        // Register events
        registerEvents();

        Logger.info("Setup Finished.");

        livesConfig.save();
        Logger.info("Config Saved.");
    }

    @Override
    protected void start() {
        if (isJemPlaceholdersEnabled()) {
            JemPlaceholdersAPI.registerExpansion(new JemLivesExpansion());
        }

        if(livesConfig.get().getUpdateCheck()){
            new UpdateChecker(version.toString()).checkForUpdatesAsync();
        }

        Logger.info("[JemLives] Started!");
        Logger.info("[JemLives] Use /jemp help for commands");
    }

    @Override
    protected void shutdown(){
        Logger.info("Shutting down...");

        if (this.kickManager != null) {
            this.kickManager.stop();
        }

        this.getCommandRegistry().shutdown();
        this.getEventRegistry().shutdown();

        livesConfig.save();
        Logger.info("Config Saved.");

        Logger.info("Shutdown Complete");
    }

    private void registerCommands() {
        this.getCommandRegistry().registerCommand(new LivesCommand("jemlives", "Main command for JemLives plugin"));
        this.getCommandRegistry().registerCommand(new LivesCommand.LivesCheckOnlyCommand());
        Logger.info("Commands Registered.");
    }

    private void registerEvents() {
        this.getEntityStoreRegistry().registerSystem(new OnPlayerDeathEvent());
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, OnPlayerConnectEvent::onPlayerConnect);
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, OnPlayerReadyEvent::onPlayerReady);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, OnPlayerLeaveEvent::onPlayerLeave);
        
        Logger.info("Events Registered.");
        Logger.debug("Debug logging is enabled.");
    }
    
    public Config<LivesConfig> getLivesConfig() {
        return this.livesConfig;
    }

    public LivesManager getLivesManager() {
        return this.livesManager;
    }

    public LivesHudManager getLivesHudManager() {
        return this.livesHudManager;
    }

    public KickManager getKickManager() {
        return this.kickManager;
    }

    /** Show the lives HUD to a player. */
    public void showLivesHud(Ref<EntityStore> ref, Store<EntityStore> store, int lives) {
        livesHudManager.show(ref, store, lives);
    }

    /** Update the lives count on the HUD for a player. */
    public void updateLivesHud(UUID uuid, int lives) {
        livesHudManager.update(uuid, lives);
    }

    /** Remove the lives HUD from a player. */
    public void removeLivesHud(Ref<EntityStore> ref, Store<EntityStore> store) {
        livesHudManager.remove(ref, store);
    }

    private boolean isJemPlaceholdersEnabled() {
        try {
            Class.forName("com.jemsire.jemplaceholders.api.JemPlaceholdersAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
