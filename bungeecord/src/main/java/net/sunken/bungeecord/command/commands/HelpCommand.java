package net.sunken.bungeecord.command.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.sunken.bungeecord.command.BungeeCommand;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;

import java.util.Arrays;
import java.util.Optional;

@Command(aliases = {"help", "?", "bukkit:help", "bukkit:?"})
public class HelpCommand extends BungeeCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        Arrays.asList(
                "WIP"
        ).forEach(message -> commandSender.sendMessage(TextComponent.fromLegacyText(message)));
        return true;
    }

}
