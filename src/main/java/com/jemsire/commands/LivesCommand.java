package com.jemsire.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.jemsire.config.LivesConfig;
import com.jemsire.plugin.JemLives;
import com.jemsire.ui.LivesInfoPage;
import com.jemsire.utils.LivesManager;
import com.jemsire.utils.PlaceholderReplacer;
import com.jemsire.utils.TinyMsg;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LivesCommand extends AbstractCommandCollection {

    /** Permission to check own lives (/lives, /jemlives check). */
    public static final String PERMISSION_CHECK = "jemlives.check";
    /** Permission to open the lives info UI (/jemlives info). */
    public static final String PERMISSION_INFO = "jemlives.info";
    /** Permission to reload config (/jemlives reload). */
    public static final String PERMISSION_RELOAD = "jemlives.reload";

    public LivesCommand(String name, String description) {
        super(name, description);
        this.addSubCommand(new CheckCommand());
        this.addSubCommand(new InfoCommand());
        this.addSubCommand(new ReloadSubCommand());
    }

    private static void sendNoPermission(CommandContext context) {
        context.sendMessage(Message.raw("You do not have permission to perform this command!").color(Color.RED));
    }

    /** Standalone /lives command — same behavior as /jemlives check. */
    public static class LivesCheckOnlyCommand extends AbstractPlayerCommand {
        public LivesCheckOnlyCommand() {
            super("lives", "Check your remaining lives");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            if (!context.sender().hasPermission(PERMISSION_CHECK)) {
                sendNoPermission(context);
                return;
            }
            JemLives plugin = JemLives.get();
            LivesManager livesManager = plugin.getLivesManager();
            LivesConfig config = plugin.getLivesConfig().get();

            int lives = livesManager.getLives(playerRef.getUuid());

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("lives", String.valueOf(lives));
            placeholders.put("player", playerRef.getUsername());

            String message = PlaceholderReplacer.replacePlaceholders(config.getLivesCommandMessage(), placeholders);
            context.sendMessage(TinyMsg.format(message));
        }
    }

    class CheckCommand extends AbstractPlayerCommand {
        CheckCommand() {
            super("check", "Check your remaining lives");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            if (!context.sender().hasPermission(PERMISSION_CHECK)) {
                sendNoPermission(context);
                return;
            }
            JemLives plugin = JemLives.get();
            LivesManager livesManager = plugin.getLivesManager();
            LivesConfig config = plugin.getLivesConfig().get();

            int lives = livesManager.getLives(playerRef.getUuid());

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("lives", String.valueOf(lives));
            placeholders.put("player", playerRef.getUsername());

            String message = PlaceholderReplacer.replacePlaceholders(config.getLivesCommandMessage(), placeholders);
            context.sendMessage(TinyMsg.format(message));
        }
    }

    class InfoCommand extends AbstractPlayerCommand {
        InfoCommand() {
            super("info", "Open lives info UI");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            if (!context.sender().hasPermission(PERMISSION_INFO)) {
                sendNoPermission(context);
                return;
            }
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) {
                context.sendMessage(Message.raw("Error: Could not get player."));
                return;
            }
            LivesInfoPage page = new LivesInfoPage(playerRef);
            player.getPageManager().openCustomPage(ref, store, page);
        }
    }

    class ReloadSubCommand extends AbstractPlayerCommand {
        ReloadSubCommand() {
            super("reload", "Reload the config for JemLives");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            if (!context.sender().hasPermission(PERMISSION_RELOAD)) {
                sendNoPermission(context);
                return;
            }

            JemLives plugin = JemLives.get();
            if (plugin != null) {
                plugin.getLivesConfig().load();
                context.sendMessage(Message.raw("Lives config reloaded.").color(Color.GREEN));
            } else {
                context.sendMessage(Message.raw("Plugin not available.").color(Color.RED));
            }
        }
    }
}
