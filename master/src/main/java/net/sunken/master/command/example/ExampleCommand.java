package net.sunken.master.command.example;

import lombok.extern.java.Log;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.master.command.MasterCommand;

import java.util.Optional;

@Log
@Command(aliases = {"masterexample", "mastertest"}, usage = "/masterexample")
public class ExampleCommand extends MasterCommand {

    @Override
    public boolean onCommand(Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            log.info(String.format("ExampleCommand executed by %s!", abstractPlayer.getUsername()));
            return true;
        }

        return false;
    }

}
