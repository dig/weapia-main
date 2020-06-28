package net.sunken.bungeecord.command;

import lombok.extern.java.Log;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.sunken.common.command.CommandResponse;
import net.sunken.common.command.impl.BaseCommand;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.command.impl.BasicCommand;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.player.PlayerManager;
import net.sunken.common.util.cooldown.Cooldowns;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Log
public class CommandHandler extends Command {

    private BaseCommandRegistry baseCommandRegistry;
    private PlayerManager playerManager;
    private Cooldowns cooldowns;

    public CommandHandler(String name, String[] aliases, BaseCommandRegistry baseCommandRegistry, PlayerManager playerManager, Cooldowns cooldowns) {
        super(name, null, aliases);
        this.baseCommandRegistry = baseCommandRegistry;
        this.playerManager = playerManager;
        this.cooldowns = cooldowns;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        Optional<BaseCommand> baseCommandOptional = baseCommandRegistry.findCommandByName(getName());

        boolean success = false;
        if (baseCommandOptional.isPresent()) {
            BaseCommand baseCommand = baseCommandOptional.get();

            Class<? extends BaseCommand> clazz = baseCommand.getClass();
            net.sunken.common.command.Command commandAnnotation = clazz.getAnnotation(net.sunken.common.command.Command.class);

            Optional<AbstractPlayer> abstractPlayerOptional = Optional.empty();
            if (commandSender instanceof ProxiedPlayer)
                abstractPlayerOptional = playerManager.get(((ProxiedPlayer) commandSender).getUniqueId());

            if (abstractPlayerOptional.isPresent()) {
                AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
                String cooldownKey = "cmd:" + getName();

                if (!cooldowns.canProceed(cooldownKey, abstractPlayer.getUuid())) {
                    commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + commandAnnotation.errorCooldown()));
                    return;
                }
            }

            Rank rank = (abstractPlayerOptional.isPresent() ? abstractPlayerOptional.get().getRank() : Rank.PLAYER);
            CommandResponse commandResponse = baseCommandRegistry.matchCommandRequirements(commandAnnotation, rank, args);
            Optional<Method> subCommandMethodOptional = (args.length > 0 ? baseCommandRegistry.findSubCommandInCommand(clazz, rank, args) : Optional.empty());

            if (subCommandMethodOptional.isPresent()) {
                Method subCommandMethod = subCommandMethodOptional.get();

                try {
                    if (baseCommand instanceof BasicCommand) {
                        BasicCommand basicCommand = (BasicCommand) baseCommand;
                        Object[] params = {Arrays.copyOfRange(args, 1, args.length)};

                        success = (Boolean) subCommandMethod.invoke(basicCommand, params);
                    } else if (baseCommand instanceof BungeeCommand) {
                        BungeeCommand bungeeCommand = (BungeeCommand) baseCommand;

                        success = (Boolean) subCommandMethod.invoke(bungeeCommand, commandSender, abstractPlayerOptional, Arrays.copyOfRange(args, 1, args.length));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.severe(String.format("Command parameters wrong for %s %s.", getName(), subCommandMethod.getName()));
                    e.printStackTrace();
                }
            } else {
                switch (commandResponse) {
                    case INVALID_RANK:
                        commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + commandAnnotation.errorPermission()));
                        break;
                    case INVALID_ARGS:
                        commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: " + commandAnnotation.usage()));
                        break;
                    case SUCCESS:
                        if (baseCommand instanceof BasicCommand) {
                            success =  ((BasicCommand) baseCommand).onCommand(args);
                        } else if (baseCommand instanceof BungeeCommand) {
                            success = ((BungeeCommand) baseCommand).onCommand(commandSender, abstractPlayerOptional, args);
                        } else {
                            log.severe(String.format("Command not handled for %s.", getName()));
                        }
                        break;
                }
            }

            if (success && abstractPlayerOptional.isPresent()) {
                AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
                cooldowns.create("cmd:" + getName(), abstractPlayer.getUuid(), System.currentTimeMillis() + commandAnnotation.cooldown());
            }
        }
    }

}
