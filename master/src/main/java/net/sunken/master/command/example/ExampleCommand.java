package net.sunken.master.command.example;

import lombok.extern.java.Log;
import net.sunken.common.command.Command;
import net.sunken.common.player.PlayerDetail;
import net.sunken.master.command.MasterCommand;

@Log
@Command(aliases = {"masterexample", "mastertest"}, usage = "/masterexample")
public class ExampleCommand extends MasterCommand {

    @Override
    public boolean onCommand(PlayerDetail playerDetail, String[] args) {
        log.info(String.format("ExampleCommand executed by %s!", playerDetail.getDisplayName()));
        return true;
    }

}
