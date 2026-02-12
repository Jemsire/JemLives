package com.jemsire.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.jemsire.config.LivesConfig;
import com.jemsire.plugin.JemLives;
import com.jemsire.utils.LivesManager;
import com.jemsire.utils.Logger;

import javax.annotation.Nonnull;

/**
 * Custom HUD that shows the player's lives count and icon.
 * Uses native Hytale CustomUIHud (see hytale-docs HUD System).
 */
public class LivesHud extends CustomUIHud {

    /** Layout path relative to Common/UI/Custom/ */
    public static final String LAYOUT = "Hud/lives_hud.ui";

    public LivesHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    /** Upper bound for dynamic life icons (avoids huge markup). */
    private static final int MAX_LIFE_ICONS_CAP = 20;

    @Override
    protected void build(UICommandBuilder builder) {
        builder.append(LAYOUT);
        buildLifeIcons(builder);
    }

    /**
     * Dynamically adds life icons to the HUD to match the server config max lives.
     * Clears the container, then appends one icon per max life via appendInline, and sets visibility by current lives.
     */
    private void buildLifeIcons(UICommandBuilder builder) {
        LivesConfig config = getConfig();
        if (config == null) return;

        if(!config.isShowLivesHud()){
            return;
        }

        int maxLives = Math.min(Math.max(1, config.getInitialLivesMax()), MAX_LIFE_ICONS_CAP);
        int lives = getLives();
        String iconPath = config.getHudIconPath() != null ? config.getHudIconPath() : "Hud/lives_icon.png";
        Logger.debug("Building lives HUD for player " + getPlayerRef().getUsername() + ": " + lives + " / " + maxLives + " icons");

        builder.clear("#lifeIconsContainer");
        for (int i = 1; i <= maxLives; i++) {
            String markup = "Group #lifeIcon" + i + " { Background: PatchStyle(TexturePath: \"" + iconPath + "\"); Visible: " + (i <= lives) + "; Anchor: (Width: 36, Height: 36, Right: 4, Left: 4); Label { Text: \"\"; } }";
            builder.appendInline("#lifeIconsContainer", markup);
        }
    }

    /** Updates visibility of life icons (current lives). Call from HudManager when lives change; icon count was set at build. */
    public void updateContent(UICommandBuilder builder) {
        LivesConfig config = getConfig();
        if (config == null) return;

        if(!config.isShowLivesHud()){
            return;
        }

        int maxLives = Math.min(Math.max(1, config.getInitialLivesMax()), MAX_LIFE_ICONS_CAP);
        int lives = getLives();
        Logger.debug("Updating lives HUD for player " + getPlayerRef().getUsername() + ": " + lives + " / " + maxLives);

        for (int i = 1; i <= maxLives; i++) {
            builder.set("#lifeIcon" + i + ".Visible", i <= lives);
        }
    }

    /** Send an incremental update to the client (e.g. after lives change). */
    public void refresh() {
        UICommandBuilder builder = new UICommandBuilder();
        updateContent(builder);
        update(false, builder);
    }

    private int getLives() {
        JemLives plugin = JemLives.get();
        if (plugin == null) return 0;
        LivesManager lm = plugin.getLivesManager();
        return lm != null ? lm.getLives(getPlayerRef().getUuid()) : 0;
    }

    private LivesConfig getConfig() {
        JemLives plugin = JemLives.get();
        if (plugin == null || plugin.getLivesConfig() == null) return null;
        return plugin.getLivesConfig().get();
    }
}
