package net.sunken.core.command;

import lombok.extern.java.Log;
import net.sunken.common.command.Command;
import net.sunken.common.command.CommandResponse;
import net.sunken.common.command.impl.BaseCommand;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.command.impl.BasicCommand;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.player.PlayerManager;
import net.sunken.common.util.cooldown.Cooldowns;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Log
public class CommandHandler extends org.bukkit.command.defaults.BukkitCommand {

    private BaseCommandRegistry baseCommandRegistry;
    private PlayerManager playerManager;
    private Cooldowns cooldowns;

    public CommandHandler(String name, BaseCommandRegistry baseCommandRegistry, PlayerManager playerManager, Cooldowns cooldowns) {
        super(name);
        this.baseCommandRegistry = baseCommandRegistry;
        this.playerManager = playerManager;
        this.cooldowns = cooldowns;
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        Optional<BaseCommand> baseCommandOptional = baseCommandRegistry.findCommandByName(label);

        boolean success = false;
        if (baseCommandOptional.isPresent()) {
            BaseCommand baseCommand = baseCommandOptional.get();

            Class<? extends BaseCommand> clazz = baseCommand.getClass();
            Command commandAnnotation = clazz.getAnnotation(Command.class);

            Optional<AbstractPlayer> abstractPlayerOptional = Optional.empty();
            if (commandSender instanceof Player)
                abstractPlayerOptional = playerManager.get(((Player) commandSender).getUniqueId());

            //--- Cooldown
            if (abstractPlayerOptional.isPresent()) {
                AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
                String cooldownKey = "cmd:" + getName();

                if (!cooldowns.canProceed(cooldownKey, abstractPlayer.getUuid())) {
                    commandSender.sendMessage(ChatColor.RED + commandAnnotation.errorCooldown());
                    return true;
                }
            }

            Rank rank = (abstractPlayerOptional.isPresent() ? abstractPlayerOptional.get().getRank() : (commandSender instanceof ConsoleCommandSender ? Rank.OWNER : Rank.PLAYER));
            CommandResponse commandResponse = baseCommandRegistry.matchCommandRequirements(commandAnnotation, rank, args);
            Optional<Method> subCommandMethodOptional = (args.length > 0 ? baseCommandRegistry.findSubCommandInCommand(clazz, rank, args) : Optional.empty());

            if (subCommandMethodOptional.isPresent()) {
                Method subCommandMethod = subCommandMethodOptional.get();

                try {
                    if (baseCommand instanceof BasicCommand) {
                        BasicCommand basicCommand = (BasicCommand) baseCommand;
                        Object[] params = {Arrays.copyOfRange(args, 1, args.length)};

                        success = (Boolean) subCommandMethod.invoke(basicCommand, params);
                    } else if (baseCommand instanceof BukkitCommand) {
                        BukkitCommand bukkitCommand = (BukkitCommand) baseCommand;

                        success = (Boolean) subCommandMethod.invoke(bukkitCommand, commandSender, abstractPlayerOptional, Arrays.copyOfRange(args, 1, args.length));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.severe(String.format("Command parameters wrong for %s %s.", label, subCommandMethod.getName()));
                    e.printStackTrace();
                }
            } else {
                switch (commandResponse) {
                    case INVALID_RANK:
                        commandSender.sendMessage(ChatColor.RED + commandAnnotation.errorPermission());
                        break;
                    case INVALID_ARGS:
                        commandSender.sendMessage(ChatColor.RED + "Usage: " + commandAnnotation.usage());
                        break;
                    case SUCCESS:
                        if (baseCommand instanceof BasicCommand) {
                            success = ((BasicCommand) baseCommand).onCommand(args);
                        } else if (baseCommand instanceof BukkitCommand) {
                            success = ((BukkitCommand) baseCommand).onCommand(commandSender, abstractPlayerOptional, args);
                        } else {
                            log.severe(String.format("Command not handled for %s.", label));
                        }

                        break;
                }
            }

            if (success && abstractPlayerOptional.isPresent()) {
                AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
                cooldowns.create("cmd:" + getName(), abstractPlayer.getUuid(), System.currentTimeMillis() + commandAnnotation.cooldown());
            }
        }

        return true;
    }
}
