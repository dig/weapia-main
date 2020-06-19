package net.sunken.master;

import com.google.inject.*;
import net.sunken.common.*;
import net.sunken.common.config.*;
import net.sunken.common.network.*;
import net.sunken.common.server.module.*;
import net.sunken.master.command.*;
import net.sunken.master.instance.*;
import net.sunken.master.kube.*;
import net.sunken.master.command.networkcommand.*;
import net.sunken.master.party.*;
import net.sunken.master.queue.*;
import net.sunken.master.reboot.*;

import java.io.*;

public class MasterModule extends AbstractModule {

    @Override
    public void configure() {
        install(new CommonModule());
        install(new NetworkModule());

        install(new ConfigModule(new File("config/common.conf"), KubeConfiguration.class));

        install(new InstanceModule());
        install(new QueueModule());
        install(new ServerModule());
        install(new PartyModule());

        install(new MasterRebootModule());
        install(new CommandModule());
        install(new NetworkCommandModule());
    }

}
