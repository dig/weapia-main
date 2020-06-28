package net.sunken.core.engine;

import com.google.inject.AbstractModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.FacetBinder;
import net.sunken.core.engine.command.DebugCommand;
import net.sunken.core.engine.state.config.WaitingConfiguration;

import java.io.File;

public class EngineModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ConfigModule(new File("config/waiting.conf"), WaitingConfiguration.class));

        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(EngineManager.class);
        facetBinder.addBinding(DebugCommand.class);
    }

}
