package net.sunken.core.team;

import com.google.inject.AbstractModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.core.engine.EngineManager;
import net.sunken.core.engine.command.DebugCommand;
import net.sunken.core.team.config.TeamConfiguration;

import java.io.File;

public class TeamModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ConfigModule(new File("config/team.conf"), TeamConfiguration.class));

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(TeamManager.class);
    }

}
