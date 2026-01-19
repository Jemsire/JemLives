package com.jemsire.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * Configuration for death message display settings.
 */
public class DeathConfig {
    private boolean showDeathMessage = true;
    private boolean showPosition = true;
    private String deathAnnouncementFormat = "<red>{player} {deathCause}";
    private String deathLocationFormat = "<gold>You last death position: <white>X:{x} Y:{y} Z:{z}";
    private String deathCauseReplacement = "was";

    public DeathConfig() {
    }

    public static final BuilderCodec<DeathConfig> CODEC =
            BuilderCodec.builder(DeathConfig.class, DeathConfig::new)
                    .append(
                            new KeyedCodec<Boolean>("ShowDeathMessage", Codec.BOOLEAN),
                            (config, value, info) -> config.showDeathMessage = value != null ? value : true,
                            (config, info) -> config.showDeathMessage
                    )
                    .add()

                    .append(
                            new KeyedCodec<Boolean>("ShowPosition", Codec.BOOLEAN),
                            (config, value, info) -> config.showPosition = value != null ? value : true,
                            (config, info) -> config.showPosition
                    )
                    .add()

                    .append(
                            new KeyedCodec<String>("DeathAnnouncementFormat", Codec.STRING),
                            (config, value, info) -> config.deathAnnouncementFormat = value != null && !value.isEmpty() ? value : "<red>{player} {deathCause}",
                            (config, info) -> config.deathAnnouncementFormat
                    )
                    .add()

                    .append(
                            new KeyedCodec<String>("DeathLocationFormat", Codec.STRING),
                            (config, value, info) -> config.deathLocationFormat = value != null && !value.isEmpty() ? value : "<gold>Your last death position: <white>X:{x} Y:{y} Z:{z}",
                            (config, info) -> config.deathLocationFormat
                    )
                    .add()

                    .append(
                            new KeyedCodec<String>("DeathCauseReplacement", Codec.STRING),
                            (config, value, info) -> config.deathCauseReplacement = value != null && !value.isEmpty() ? value : "was",
                            (config, info) -> config.deathCauseReplacement
                    )
                    .add()

                    .build();

    public boolean isShowDeathMessage() {
        return showDeathMessage;
    }

    public boolean isShowPosition() {
        return showPosition;
    }

    public String getDeathAnnouncementFormat() {
        return deathAnnouncementFormat;
    }

    public String getDeathLocationFormat() {
        return deathLocationFormat;
    }

    public String getDeathCauseReplacement() {
        return deathCauseReplacement;
    }
}
