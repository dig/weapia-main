package net.sunken.bungeecord.command;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.md_5.bungee.api.plugin.Plugin;
import net.sunken.common.command.Command;
import net.sunken.common.command.impl.BaseCommand;
import net.sunken.common.command.impl.BaseCommandRegistry;

@Log
public class CommandRegistry extends BaseCommandRegistry {

    @Inject
    private Plugin plugin;
    @Inject
    private CommandHandlerFactory commandHandlerFactory;

    public CommandRegistry() { registeredCommands = Sets.newLinkedHashSet(); }

    @Override
    public void register(BaseCommand baseCommand) {
        registeredCommands.add(baseCommand);

        Class<? extends BaseCommand> clazz = baseCommand.getClass();
        Command commandAnnotation = clazz.getAnnotation(Command.class);
        String name = commandAnnotation.aliases()[0];

        plugin.getProxy().getPluginManager().registerCommand(plugin, commandHandlerFactory.createHandler(name, commandAnnotation.aliases()));
        log.info(String.format("Registered command: %s.", name));
    }

    @Override
    public void unregister(BaseCommand baseCommand) {
        registeredCommands.remove(baseCommand);

        // TODO: unregister from bungeecord
    }

}
