package com.jemsire.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.jemsire.plugin.JemDeaths;

import javax.annotation.Nonnull;
import java.awt.*;

public class ReloadCommand extends CommandBase {

    public ReloadCommand(String name, String description) {
        super(name, description);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        if(context.isPlayer()){
            if(!context.sender().hasPermission("jemdeaths.reload")){
                context.sendMessage(Message.raw("You do not have permission to perform this command!").color(Color.RED));
                return;
            }
        }

        JemDeaths plugin = JemDeaths.get();
        if (plugin != null) {
            plugin.getDeathConfig().load();
            context.sendMessage(Message.raw("Death config reloaded.").color(Color.GREEN));
        } else {
            context.sendMessage(Message.raw("Plugin not available.").color(Color.RED));
        }
    }
}
