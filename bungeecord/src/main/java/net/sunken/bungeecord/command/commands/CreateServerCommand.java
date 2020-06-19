package net.sunken.bungeecord.command.commands;

import com.google.inject.Inject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.command.BungeeCommand;
import net.sunken.common.command.Command;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.packet.RequestServerCreationPacket;
import net.sunken.common.util.AsyncHelper;

import java.util.Optional;

@Command(aliases = {"createserver", "cs"}, rank = Rank.DEVELOPER, usage = "/createserver <type> <game>", min = 2, max = 2)
public class CreateServerCommand extends BungeeCommand {

    @Inject
    private PacketUtil packetUtil;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        try {
            Server.Type type = Server.Type.valueOf(args[0]);
            Game game = Game.valueOf(args[1]);

            AsyncHelper.executor().submit(() -> packetUtil.send(new RequestServerCreationPacket(type, game)));
            commandSender.sendMessage(TextComponent.fromLegacyText(Constants.COMMAND_CREATESERVER_SUCCESS));
            return true;
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(TextComponent.fromLegacyText(Constants.COMMAND_CREATESERVER_INVALID));
        }

        return false;
    }

}
