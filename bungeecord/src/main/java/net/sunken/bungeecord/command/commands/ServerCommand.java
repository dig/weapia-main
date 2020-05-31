package net.sunken.bungeecord.command.commands;

import com.google.inject.Inject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.command.BungeeCommand;
import net.sunken.common.command.Command;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;

import java.util.Optional;

@Command(aliases = {"server", "goto", "go", "play"}, usage = "/server <game>", min = 1, max = 1, cooldown = 5000L)
public class ServerCommand extends BungeeCommand {

    @Inject
    private PacketUtil packetUtil;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            String target = args[0].toUpperCase();

            try {
                Game game = Game.valueOf(target);

                if (game != Game.NONE) {
                    commandSender.sendMessage(TextComponent.fromLegacyText(String.format(Constants.COMMAND_SEND_TO_GAME, game.getFriendlyName())));
                    packetUtil.send(new PlayerRequestServerPacket(abstractPlayer.getUuid(), Server.Type.INSTANCE, game, true));
                    return true;
                } else {
                    commandSender.sendMessage(TextComponent.fromLegacyText(Constants.COMMAND_SERVER_INVALID));
                }
            } catch (IllegalArgumentException ex) {
                commandSender.sendMessage(TextComponent.fromLegacyText(Constants.COMMAND_SERVER_INVALID));
            }
        }

        return false;
    }

}
