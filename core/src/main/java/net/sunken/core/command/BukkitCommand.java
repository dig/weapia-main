package net.sunken.core.command;

import net.sunken.common.command.impl.BaseCommand;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.AbstractPlayer;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public abstract class BukkitCommand extends BaseCommand implements Facet {

    public abstract boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args);

}
