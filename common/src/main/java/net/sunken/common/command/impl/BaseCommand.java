package net.sunken.common.command.impl;

import net.sunken.common.command.*;

public abstract class BaseCommand {

    public Command getCommand() {
        return getClass().getAnnotation(Command.class);
    }
}
