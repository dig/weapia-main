package net.sunken.bungeecord.command.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.sunken.bungeecord.command.BungeeCommand;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;

import java.util.Arrays;
import java.util.Optional;

@Command(aliases = {"discord", "disc", "join", "community"})
public class DiscordCommand extends BungeeCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        Arrays.asList(
                " ",
                "&a&lCommunity Chat - Discord",
                "&fUpdates. Text & Voice Chat. Meet other players.",
                " "
        ).forEach(message -> commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message))));

        TextComponent clickableLink = new TextComponent("[JOIN]");
        clickableLink.setColor(ChatColor.GREEN);
        clickableLink.setBold(true);
        clickableLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/m2h4Gxt"));
        commandSender.sendMessage(clickableLink);

        commandSender.sendMessage(new TextComponent(" "));
        return true;
    }

}
