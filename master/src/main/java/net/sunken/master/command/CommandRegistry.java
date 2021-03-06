package net.sunken.master.command;

import com.google.common.collect.*;
import com.google.inject.*;
import lombok.extern.java.*;
import net.sunken.common.command.*;
import net.sunken.common.command.impl.*;
import net.sunken.common.inject.*;

@Log
@Singleton
public class CommandRegistry extends BaseCommandRegistry implements Facet {

    public CommandRegistry() {
        registeredCommands = Sets.newLinkedHashSet();
    }

    @Override
    public void register(BaseCommand baseCommand) {
        registeredCommands.add(baseCommand);

        Class<? extends BaseCommand> clazz = baseCommand.getClass();
        Command commandAnnotation = clazz.getAnnotation(Command.class);
        String name = commandAnnotation.aliases()[0];

        log.info(String.format("Registered command: %s.", name));
    }

    @Override
    public void unregister(BaseCommand baseCommand) {
        registeredCommands.remove(baseCommand);
    }
}
