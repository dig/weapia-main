package net.sunken.master;

import com.google.inject.AbstractModule;
import net.sunken.common.CommonModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.network.NetworkModule;
import net.sunken.master.instance.InstanceModule;
import net.sunken.master.kube.KubeConfiguration;
import net.sunken.master.party.PartyModule;
import net.sunken.master.queue.QueueModule;
import net.sunken.common.server.module.ServerModule;

import java.io.File;

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
    }

}
