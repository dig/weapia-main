package net.sunken.core.command;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.command.Command;
import net.sunken.common.command.impl.BaseCommand;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.core.Constants;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Log
@Singleton
public class CommandRegistry extends BaseCommandRegistry implements Facet, Enableable, Listener {

    @Inject
    private CommandHandlerFactory commandHandlerFactory;

    public CommandRegistry() { registeredCommands = Sets.newLinkedHashSet(); }

    @Override
    public void register(BaseCommand baseCommand) {
        registeredCommands.add(baseCommand);

        Class<? extends BaseCommand> clazz = baseCommand.getClass();
        Command commandAnnotation = clazz.getAnnotation(Command.class);
        String name = commandAnnotation.aliases()[0];

        CommandHandler commandHandler = commandHandlerFactory.createHandler(name);
        commandHandler.setAliases(Arrays.asList(commandAnnotation.aliases()));
        commandHandler.setDescription(commandAnnotation.desc());
        commandHandler.setName(name);
        commandHandler.setLabel(name);

        this.registerCommand(name, commandHandler);
        log.info(String.format("Registered command: %s.", name));
    }

    @Override
    public void unregister(BaseCommand baseCommand) {
        registeredCommands.remove(baseCommand);

        //--- TODO: remove from commandMap
    }

    private Optional<CommandMap> findCommandMap() {
        try {
            Field bukkitCommandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getPluginManager());

            return Optional.of(commandMap);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private void registerCommand(String fallback, BukkitCommand command) {
        Optional<CommandMap> commandMapOptional = findCommandMap();

        if (commandMapOptional.isPresent()) {
            CommandMap commandMap = commandMapOptional.get();
            commandMap.register(fallback, command);
        } else {
            log.severe("Unable to find commandMap via reflection.");
        }
    }

    private void unregisterCommand(@NonNull String name) {
        Optional<CommandMap> commandMapOptional = findCommandMap();

        if (commandMapOptional.isPresent()) {
            CommandMap commandMap = commandMapOptional.get();

            try {
                Class<SimpleCommandMap> simpleCommandMap = (Class<SimpleCommandMap>) commandMap.getClass();
                final Field knownCommands = simpleCommandMap.getDeclaredField("knownCommands");
                knownCommands.setAccessible(true);

                Map<String, org.bukkit.command.Command> cmds = (Map<String, org.bukkit.command.Command>) knownCommands.get(commandMap);
                cmds.remove(name);

                knownCommands.set(commandMap, cmds);
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        } else {
            log.severe("Unable to find commandMap via reflection.");
        }
    }

    private void unregisterAllCommands() {
        Optional<CommandMap> commandMapOptional = findCommandMap();

        if (commandMapOptional.isPresent()) {
            CommandMap commandMap = commandMapOptional.get();

            try {
                Class<SimpleCommandMap> simpleCommandMap = (Class<SimpleCommandMap>) commandMap.getClass();
                final Field knownCommands = simpleCommandMap.getDeclaredField("knownCommands");
                knownCommands.setAccessible(true);

                Map<String, org.bukkit.command.Command> cmds = (Map<String, org.bukkit.command.Command>) knownCommands.get(commandMap);
                cmds.keySet().removeIf(label -> !Constants.WHITELISTED_DEFAULT_COMMANDS.contains(label));

                knownCommands.set(commandMap, cmds);
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        } else {
            log.severe("Unable to find commandMap via reflection.");
        }
    }

    @Override
    public void enable() {
        unregisterAllCommands();
    }

    @Override
    public void disable() {
    }

}
