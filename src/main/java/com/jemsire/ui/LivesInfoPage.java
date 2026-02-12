package com.jemsire.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.jemsire.config.LivesConfig;
import com.jemsire.plugin.JemLives;
import com.jemsire.utils.LivesManager;

import javax.annotation.Nonnull;

/**
 * Lives info page built from MCP Custom UI docs (api/server-internals/custom-ui).
 * Layout: Common/UI/Custom/JemLives/LivesInfoPage.ui
 */
public class LivesInfoPage extends InteractiveCustomUIPage<LivesInfoPage.PageEventData> {

    /** Path relative to Common/UI/Custom/ */
    public static final String LAYOUT = "Hud/LivesInfoPage.ui";

    private final PlayerRef playerRef;

    public LivesInfoPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, PageEventData.CODEC);
        this.playerRef = playerRef;
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        cmd.append(LAYOUT);

        int lives = 0;
        String regenText = "";
        JemLives plugin = JemLives.get();
        if (plugin != null) {
            LivesManager lm = plugin.getLivesManager();
            lives = lm.getLives(playerRef.getUuid());
            long regenSeconds = lm.getRemainingRegenTime(playerRef.getUuid());
            regenText = regenSeconds > 0
                    ? "Next life in: " + formatTime(regenSeconds)
                    : (lives > 0 ? "Fully regenerated." : "Regenerating...");
        }
        cmd.set("#LivesLabel.Text", "Lives: " + lives);
        cmd.set("#RegenLabel.Text", regenText);

        if (plugin != null) {
            LivesConfig config = plugin.getLivesConfig().get();
            int maxLives = config.getInitialLivesMax();
            int regenMinutes = config.getRegenTimeMinutes();
            String zeroAction = config.getZeroLivesAction();
            String actionDesc = "KICK".equalsIgnoreCase(zeroAction)
                    ? "you are kicked and must wait to rejoin."
                    : "you are put in spectator until lives regenerate.";
            String[] lines = {
                    "• You have up to " + maxLives + " lives. When you die, you lose one.",
                    "• If you lose all lives, " + actionDesc,
                    "• After " + regenMinutes + " minutes, your lives regenerate and you can play again.",
                    "• Use the HUD and /lives to see your current lives and regen time.",
                    "",
                    ""
            };
            cmd.set("#ExplanationLine1.Text", lines[0]);
            cmd.set("#ExplanationLine2.Text", lines[1]);
            cmd.set("#ExplanationLine3.Text", lines[2]);
            cmd.set("#ExplanationLine4.Text", lines[3]);
            cmd.set("#ExplanationLine5.Text", lines.length > 4 ? lines[4] : "");
            cmd.set("#ExplanationLine6.Text", lines.length > 5 ? lines[5] : "");
        }

        evt.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CloseBtn",
                new EventData().append("Action", "close"),
                false
        );
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull PageEventData data
    ) {
        if ("close".equals(data.action)) {
            close();
        }
    }

    private static String formatTime(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        StringBuilder sb = new StringBuilder();
        if (h > 0) sb.append(h).append("h ");
        if (m > 0) sb.append(m).append("m ");
        sb.append(s).append("s");
        return sb.toString();
    }

    public static class PageEventData {
        public static final BuilderCodec<PageEventData> CODEC = BuilderCodec.builder(
                        PageEventData.class, PageEventData::new
                )
                .append(new KeyedCodec<>("Action", Codec.STRING), (e, v) -> e.action = v, e -> e.action)
                .add()
                .build();

        private String action;

        public PageEventData() {}
    }
}
