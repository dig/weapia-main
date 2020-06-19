package net.sunken.master.command;

import net.sunken.common.command.impl.*;
import net.sunken.common.inject.*;
import net.sunken.common.player.*;

import java.util.*;

public abstract class MasterCommand extends BaseCommand implements Facet {

    public abstract boolean onCommand(PlayerDetail playerDetail, String[] args);

}
