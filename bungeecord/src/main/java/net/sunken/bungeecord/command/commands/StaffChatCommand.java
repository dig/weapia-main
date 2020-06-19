package net.sunken.bungeecord.command.commands;

import com.google.inject.Inject;
import net.md_5.bungee.api.CommandSender;
import net.sunken.bungeecord.chat.packet.StaffMessagePacket;
import net.sunken.bungeecord.command.BungeeCommand;
import net.sunken.common.command.Command;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;

import java.util.Optional;

@Command(aliases = {"adminchat", "a", "ac", "staffchat", "sc"}, rank = Rank.MOD, usage = "/adminchat <message>", min = 1)
public class StaffChatCommand extends BungeeCommand {

    @Inject
    private PacketUtil packetUtil;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();

            packetUtil.send(new StaffMessagePacket(abstractPlayer.toPlayerDetail(), Rank.MOD, String.join(" ", args)));
            return true;
        }

        return false;
    }

}
