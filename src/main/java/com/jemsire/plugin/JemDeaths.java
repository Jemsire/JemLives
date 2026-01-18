package com.jemsire.plugin;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.jemsire.commands.ReloadCommand;
import com.jemsire.config.DeathConfig;
import com.jemsire.events.OnPlayerDeathEvent;
import com.jemsire.utils.Logger;

import javax.annotation.Nonnull;

public class JemDeaths extends JavaPlugin {
    private static JemDeaths instance;

    public static JemDeaths get() {
        return instance;
    }

    private final Config<DeathConfig> deathConfig;

    public JemDeaths(@Nonnull JavaPluginInit init) {
        super(init);
        Logger.info("Starting JemDeaths Plugin...");

        // Registers the death message configuration
        this.deathConfig = this.withConfig("DeathConfig", DeathConfig.CODEC);
    }

    @Override
    protected void setup() {
        instance = this;

        // Register commands
        registerCommands();

        // Register events
        registerEvents();

        Logger.info("Setup Finished.");

        deathConfig.save();
        Logger.info("Config Saved.");
    }

    @Override
    protected void shutdown(){
        Logger.info("Shutting down...");

        this.getCommandRegistry().shutdown();
        this.getEventRegistry().shutdown();

        deathConfig.save();
        Logger.info("Config Saved.");

        Logger.info("Shutdown Complete");
    }

    private void registerCommands() {
        this.getCommandRegistry().registerCommand(new ReloadCommand("jemdeaths-reload", "Reload the config for JemDeaths"));
        Logger.info("Commands Registered.");
    }

    private void registerEvents() {
        this.getEntityStoreRegistry().registerSystem(new OnPlayerDeathEvent());
        Logger.info("Events Registered.");
    }
    
    public Config<DeathConfig> getDeathConfig() {
        return this.deathConfig;
    }
}
