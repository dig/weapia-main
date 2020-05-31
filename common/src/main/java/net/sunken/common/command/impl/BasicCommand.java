package net.sunken.common.command.impl;

import net.sunken.common.inject.Facet;

public abstract class BasicCommand extends BaseCommand implements Facet {

    public abstract boolean onCommand(String[] args);

}
