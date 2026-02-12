package com.jemsire.expansion;

import com.jemsire.jemplaceholders.api.PlaceholderExpansion;
import com.jemsire.plugin.JemLives;
import com.jemsire.utils.LivesManager;

/**
 * JemLives placeholders.
 */
public class JemLivesExpansion extends PlaceholderExpansion {

    @Override public String getIdentifier() { return "jemlives"; }

    @Override public String getName() {
        return "JemLives";
    }

    @Override public String getPluginNamespace() {
        return "Jemsire:JemLives";
    }

    @Override public String getDescription() {
        return "Provides placeholders for JemLives information.";
    }

    @Override public String getAuthor() { return "Jemsire"; }

    @Override public String getVersion() { return "1.0.0"; }

    @Override public String getWebsite() { return "https://www.curseforge.com/members/jemsire/projects"; }

    public JemLivesExpansion() {

        exact("lives", (player, params) -> {
            JemLives plugin = JemLives.get();
            LivesManager livesManager = plugin.getLivesManager();
            int lives = livesManager.getLives(player.getUuid());
            return "" + lives;
        });
    }
}
