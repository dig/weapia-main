package net.sunken.bungeecord.party;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.sunken.bungeecord.command.BungeeCommand;
import net.sunken.common.command.Command;
import net.sunken.common.command.SubCommand;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.*;
import net.sunken.common.player.AbstractPlayer;

import java.util.Arrays;
import java.util.Optional;

@Command(aliases = {"party", "p", "parties"}, cooldown = 500L)
public class PartyCommand extends BungeeCommand {

    @Inject
    private PacketUtil packetUtil;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (args.length <= 0) {
            Arrays.asList(
                    " ",
                    " &d&lPARTY",
                    " &fTeam up and conquer!",
                    " ",
                    " &aCreate &f\u27B2 &d/party create",
                    " &aDisband &f\u27B2 &5/party disband",
                    " &aLeave &f\u27B2 &d/party leave",
                    " &aInvite &f\u27B2 &5/party invite <username>",
                    " &aJoin &f\u27B2 &d/party join <username>",
                    " &aChat &f\u27B2 &5/party chat <message>",
                    " &aSet Leader &f\u27B2 &d/party setleader <username>",
                    " &aKick &f\u27B2 &5/party kick <username>",
                    " ",
                    " &fYou can also type /party <name> to quickly party a player."
            ).forEach(message -> commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message))));
            return true;
        } else if (args.length == 1 && abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            packetUtil.send(new PartyInvitePacket(abstractPlayer.toPlayerDetail(), args[0]));
            return true;
        }

        return false;
    }

    @SubCommand(aliases = {"create", "new", "start", "make"})
    public boolean create(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            packetUtil.send(new PartyCreatePacket(abstractPlayer.toPlayerDetail()));
            return true;
        }

        return false;
    }

    @SubCommand(aliases = {"disband"})
    public boolean disband(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            packetUtil.send(new PartyDisbandPacket(abstractPlayer.getUuid()));
            return true;
        }

        return false;
    }

    @SubCommand(aliases = {"invite", "inv", "add"}, usage = "/party invite <username>", min = 1, max = 1)
    public boolean invite(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            packetUtil.send(new PartyInvitePacket(abstractPlayer.toPlayerDetail(), args[0]));
            return true;
        }

        return false;
    }

    @SubCommand(aliases = {"join", "accept"}, usage = "/party join <username>", min = 1, max = 1)
    public boolean join(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            packetUtil.send(new PartyInviteFinishPacket(abstractPlayer.getUuid(), args[0], PartyInviteFinishPacket.State.ACCEPT));
            return true;
        }

        return false;
    }

    @SubCommand(aliases = {"deny", "decline"}, usage = "/party deny <username>", min = 1, max = 1)
    public boolean deny(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            packetUtil.send(new PartyInviteFinishPacket(abstractPlayer.getUuid(), args[0], PartyInviteFinishPacket.State.DENY));
            return true;
        }

        return false;
    }

    @SubCommand(aliases = {"chat", "msg", "message"}, usage = "/party chat <message>", min = 1)
    public boolean chat(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            String message = String.join(" ", args);

            packetUtil.send(new PartyMessageRequestPacket(abstractPlayer.toPlayerDetail(), message));
            return true;
        }

        return false;
    }

    @SubCommand(aliases = {"leave", "quit"})
    public boolean leave(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();

            packetUtil.send(new PartyLeavePacket(abstractPlayer.toPlayerDetail()));
            return true;
        }

        return false;
    }

    @SubCommand(aliases = {"setleader", "changeleader", "makeleader"}, usage = "/party setleader <username>", min = 1, max = 1)
    public boolean leader(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();

            packetUtil.send(new PartySetLeaderPacket(abstractPlayer.getUuid(), args[0]));
            return true;
        }

        return false;
    }

    @SubCommand(aliases = {"kick", "remove", "delete"}, usage = "/party kick <username>", min = 1, max = 1)
    public boolean kick(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();

            packetUtil.send(new PartyKickPacket(abstractPlayer.getUuid(), args[0]));
            return true;
        }

        return false;
    }

}
