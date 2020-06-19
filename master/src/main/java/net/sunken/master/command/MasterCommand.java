package net.sunken.master.command;

import net.sunken.common.command.impl.*;
import net.sunken.common.inject.*;
import net.sunken.common.player.*;
import org.bukkit.command.*;

import java.util.*;

public abstract class MasterCommand extends BaseCommand implements Facet {

    public abstract boolean onCommand(Optional<AbstractPlayer> abstractPlayer, String[] args);

}
