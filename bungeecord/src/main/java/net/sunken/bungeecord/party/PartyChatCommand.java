package net.sunken.bungeecord.party;

import com.google.inject.Inject;
import net.md_5.bungee.api.CommandSender;
import net.sunken.bungeecord.command.BungeeCommand;
import net.sunken.common.command.Command;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.PartyMessageRequestPacket;
import net.sunken.common.player.AbstractPlayer;

import java.util.Optional;

@Command(aliases = {"partychat", "pc"}, usage = "/partychat <message>", min = 1)
public class PartyChatCommand extends BungeeCommand {

    @Inject
    private PacketUtil packetUtil;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            String message = String.join(" ", args);

            packetUtil.send(new PartyMessageRequestPacket(abstractPlayer.toPlayerDetail(), message));
            return true;
        }

        return false;
    }

}
