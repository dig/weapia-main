package net.sunken.bungeecord.command.commands;

import com.google.inject.Inject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.command.BungeeCommand;
import net.sunken.common.command.Command;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;

import java.util.Optional;

@Command(aliases = {"lobby", "hub"}, cooldown = 5000L)
public class LobbyCommand extends BungeeCommand {

    @Inject
    private PacketUtil packetUtil;
    @Inject
    private ServerManager serverManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (commandSender instanceof ProxiedPlayer && abstractPlayerOptional.isPresent()) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();

            Optional<Server> serverOptional = serverManager.findServerById(proxiedPlayer.getServer().getInfo().getName());
            if (serverOptional.isPresent()) {
                Server server = serverOptional.get();

                if (server.getType() != Server.Type.LOBBY) {
                    packetUtil.send(new PlayerRequestServerPacket(abstractPlayer.getUuid(), Server.Type.LOBBY, true));
                    return true;
                } else {
                    commandSender.sendMessage(TextComponent.fromLegacyText(Constants.COMMAND_ALREADY_IN_LOBBY));
                }
            }
        }

        return false;
    }

}
