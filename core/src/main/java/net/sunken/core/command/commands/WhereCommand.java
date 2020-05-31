package net.sunken.core.command.commands;

import com.google.inject.Inject;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.server.Server;
import net.sunken.core.Constants;
import net.sunken.core.PluginInform;
import net.sunken.core.command.BukkitCommand;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@Command(aliases = {"where", "whereami", "whichserver"})
public class WhereCommand extends BukkitCommand {

    @Inject
    private PluginInform pluginInform;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        Server server = pluginInform.getServer();
        commandSender.sendMessage(String.format(Constants.COMMAND_WHERE, server.getId(), (server.getType() == Server.Type.LOBBY ? "LOBBY" : server.getGame().toString())));
        return true;
    }

}
