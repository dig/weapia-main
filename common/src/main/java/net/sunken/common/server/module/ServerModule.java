package net.sunken.common.server.module;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.FacetBinder;

public class ServerModule extends AbstractModule {

    @Override
    public void configure() {
        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(ServerManager.class);
    }
}
