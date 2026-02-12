package com.jemsire.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * Configuration for JemLives plugin.
 */
public class LivesConfig {
    private int initialLivesMin = 3;
    private int initialLivesMax = 3;
    private boolean loseLivesFromPvpOnly = false;
    private boolean gainLivesFromKills = false;
    private double gainLifeChance = 0.1;
    private String zeroLivesAction = "KICK"; // KICK or SPECTATOR
    private int regenTimeMinutes = 60;
    private String logLevel = "INFO";
    
    private String deathAnnouncementFormat = "<orange>{player} <red>has died and now has <orange>{lives} <red>lives.";
    private String localDeathMessage = "<red>You died! You have <orange>{lives} <red>lives left.";
    private String livesCommandMessage = "<green>You have {lives} lives left.";
    private String kickMessage = "You have run out of lives! Come back in {time}.";
    private String deathCauseReplacement = "was";
    private boolean showLivesHud = true;
    private String hudIconPath = "Hud/lives_icon.png";
    private boolean updateCheck = true;

    public LivesConfig() {
    }

    public static final BuilderCodec<LivesConfig> CODEC =
            BuilderCodec.builder(LivesConfig.class, LivesConfig::new)
                    .append(
                            new KeyedCodec<Integer>("InitialLivesMin", Codec.INTEGER),
                            (config, value, info) -> config.initialLivesMin = value != null ? value : 3,
                            (config, info) -> config.initialLivesMin
                    ).add()
                    .append(
                            new KeyedCodec<Integer>("InitialLivesMax", Codec.INTEGER),
                            (config, value, info) -> config.initialLivesMax = value != null ? value : 3,
                            (config, info) -> config.initialLivesMax
                    ).add()
                    .append(
                            new KeyedCodec<Boolean>("LoseLivesFromPvpOnly", Codec.BOOLEAN),
                            (config, value, info) -> config.loseLivesFromPvpOnly = value != null ? value : false,
                            (config, info) -> config.loseLivesFromPvpOnly
                    ).add()
                    .append(
                            new KeyedCodec<Boolean>("GainLivesFromKills", Codec.BOOLEAN),
                            (config, value, info) -> config.gainLivesFromKills = value != null ? value : false,
                            (config, info) -> config.gainLivesFromKills
                    ).add()
                    .append(
                            new KeyedCodec<Double>("GainLifeChance", Codec.DOUBLE),
                            (config, value, info) -> config.gainLifeChance = value != null ? value : 0.1,
                            (config, info) -> config.gainLifeChance
                    ).add()
                    .append(
                            new KeyedCodec<String>("ZeroLivesAction", Codec.STRING),
                            (config, value, info) -> config.zeroLivesAction = value != null ? value : "KICK",
                            (config, info) -> config.zeroLivesAction
                    ).add()
                    .append(
                            new KeyedCodec<Integer>("RegenTimeMinutes", Codec.INTEGER),
                            (config, value, info) -> config.regenTimeMinutes = value != null ? value : 60,
                            (config, info) -> config.regenTimeMinutes
                    ).add()
                    .append(
                            new KeyedCodec<String>("LogLevel", Codec.STRING),
                            (config, value, info) -> config.logLevel = value != null ? value : "INFO",
                            (config, info) -> config.logLevel
                    ).add()
                    .append(
                            new KeyedCodec<String>("DeathAnnouncementFormat", Codec.STRING),
                            (config, value, info) -> config.deathAnnouncementFormat = value != null ? value : "<red>{player} has died and is now on {lives} lives.",
                            (config, info) -> config.deathAnnouncementFormat
                    ).add()
                    .append(
                            new KeyedCodec<String>("LocalDeathMessage", Codec.STRING),
                            (config, value, info) -> config.localDeathMessage = value != null ? value : "<red>You died! You have {lives} lives left.",
                            (config, info) -> config.localDeathMessage
                    ).add()
                    .append(
                            new KeyedCodec<String>("LivesCommandMessage", Codec.STRING),
                            (config, value, info) -> config.livesCommandMessage = value != null ? value : "<green>You have {lives} lives left.",
                            (config, info) -> config.livesCommandMessage
                    ).add()
                    .append(
                            new KeyedCodec<String>("KickMessage", Codec.STRING),
                            (config, value, info) -> config.kickMessage = value != null ? value : "You have run out of lives! Come back in {time}.",
                            (config, info) -> config.kickMessage
                    ).add()
                    .append(
                            new KeyedCodec<String>("DeathCauseReplacement", Codec.STRING),
                            (config, value, info) -> config.deathCauseReplacement = value != null ? value : "was",
                            (config, info) -> config.deathCauseReplacement
                    ).add()
                    .append(
                            new KeyedCodec<Boolean>("ShowLivesHud", Codec.BOOLEAN),
                            (config, value, info) -> config.showLivesHud = value != null ? value : true,
                            (config, info) -> config.showLivesHud
                    ).add()
                    .append(
                            new KeyedCodec<String>("HudIconPath", Codec.STRING),
                            (config, value, info) -> config.hudIconPath = value != null && !value.isEmpty() ? value : "Hud/lives_icon.png",
                            (config, info) -> config.hudIconPath
                    ).add()
                    .append(
                            new KeyedCodec<Boolean>("UpdateCheck", Codec.BOOLEAN),
                            (config, value, info) -> config.updateCheck =  value != null ? value : true,
                            (config, info) -> config.updateCheck
                    ).add()
                    .build();

    public int getInitialLivesMin() { return initialLivesMin; }
    public int getInitialLivesMax() { return initialLivesMax; }
    public boolean isLoseLivesFromPvpOnly() { return loseLivesFromPvpOnly; }
    public boolean isGainLivesFromKills() { return gainLivesFromKills; }
    public double getGainLifeChance() { return gainLifeChance; }
    public String getZeroLivesAction() { return zeroLivesAction; }
    public int getRegenTimeMinutes() { return regenTimeMinutes; }
    public String getLogLevel() { return logLevel; }
    public String getDeathAnnouncementFormat() { return deathAnnouncementFormat; }
    public String getLocalDeathMessage() { return localDeathMessage; }
    public String getLivesCommandMessage() { return livesCommandMessage; }
    public String getKickMessage() { return kickMessage; }
    public String getDeathCauseReplacement() { return deathCauseReplacement; }
    public boolean isShowLivesHud() { return showLivesHud; }
    public String getHudIconPath() { return hudIconPath; }
    public Boolean getUpdateCheck() { return updateCheck; }
}
