package net.sunken.common.network;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.FacetBinder;

public class NetworkModule extends AbstractModule {

    @Override
    public void configure() {
        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(NetworkManager.class);
    }
}
