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
import net.sunken.common.player.packet.PlayerRequestServerIDPacket;

import java.util.Optional;

@Command(aliases = {"gotoserver", "gotos"}, rank = Rank.DEVELOPER, usage = "/gotoserver <id>", min = 1, max = 1)
public class GoToServerCommand extends BungeeCommand {

    @Inject
    private PacketUtil packetUtil;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        packetUtil.send(new PlayerRequestServerIDPacket(abstractPlayerOptional.get().getUuid(), args[0]));
        commandSender.sendMessage(TextComponent.fromLegacyText(String.format(Constants.COMMAND_GOTOSERVER_SUCCESS, args[0])));
        return true;
    }

}
