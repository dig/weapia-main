package net.sunken.common.command.example;

import net.sunken.common.command.Command;
import net.sunken.common.command.impl.BasicCommand;
import net.sunken.common.command.SubCommand;
import net.sunken.common.player.Rank;

@Command(aliases = {"example", "examplecommand"})
public class ExampleCommand extends BasicCommand {

    // /example or /examplecommand
    @Override
    public boolean onCommand(String[] args) {
        System.out.println("Example parent");
        return true;
    }

    // /example sum 1 1
    @SubCommand(
       aliases = {"sum", "calc"},
       rank = Rank.DEVELOPER,
       min = 2,
       max = 2
    )
    public boolean sum(String[] args) {
        System.out.println(args.length);
        System.out.println(args.toString());
        return true;
    }
}
