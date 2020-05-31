package net.sunken.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.sunken.common.command.impl.BaseCommand;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.AbstractPlayer;

import java.util.Optional;

public abstract class BungeeCommand extends BaseCommand implements Facet {

    public abstract boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args);

}
