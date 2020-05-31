package net.sunken.common.command.impl;

import lombok.NonNull;
import net.sunken.common.command.Command;
import net.sunken.common.command.CommandResponse;
import net.sunken.common.command.SubCommand;
import net.sunken.common.player.Rank;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

public abstract class BaseCommandRegistry {

    protected Set<BaseCommand> registeredCommands;

    public abstract void register(BaseCommand baseCommand);

    public abstract void unregister(BaseCommand baseCommand);

    public Optional<BaseCommand> findCommandByName(@NonNull String name) {
        for (BaseCommand baseCommand : registeredCommands) {
            Class<? extends BaseCommand> clazz = baseCommand.getClass();

            if (clazz.isAnnotationPresent(Command.class)) {
                Command commandAnnotation = clazz.getAnnotation(Command.class);

                for (String alias : commandAnnotation.aliases()) {
                    if (name.equalsIgnoreCase(alias)) {
                        return Optional.of(baseCommand);
                    }
                }
            }
        }

        return Optional.empty();
    }

    public CommandResponse matchCommandRequirements(@NonNull Command commandAnnotation, @NonNull Rank rank, String[] args) {
        if (rank.has(commandAnnotation.rank())) {
            if (args.length >= commandAnnotation.min() && (commandAnnotation.max() <= 0 || commandAnnotation.max() >= args.length)) {
                return CommandResponse.SUCCESS;
            } else {
                return CommandResponse.INVALID_ARGS;
            }
        } else {
            return CommandResponse.INVALID_RANK;
        }
    }

    public Optional<Method> findSubCommandInCommand(@NonNull Class<? extends BaseCommand> clazz, @NonNull Rank rank, String[] args) {
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommandAnnotation = method.getAnnotation(SubCommand.class);
                boolean validSubCommandAlias = false;

                for (String alias : subCommandAnnotation.aliases()) {
                    if (alias.equalsIgnoreCase(args[0]))
                        validSubCommandAlias = true;
                }

                if (validSubCommandAlias && rank.has(subCommandAnnotation.rank())) {
                    if ((args.length - 1) >= subCommandAnnotation.min() && (subCommandAnnotation.max() <= 0 || subCommandAnnotation.max() >= (args.length - 1))) {
                        return Optional.of(method);
                    }
                }
            }
        }

        return Optional.empty();
    }

}
