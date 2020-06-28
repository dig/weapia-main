package net.sunken.core.networkcommand;

import com.google.inject.*;
import net.sunken.common.inject.*;

public class NetworkCommandModule extends AbstractModule {

    @Override
    protected void configure() {
        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(NetworkCommandListener.class);
        facetBinder.addBinding(AvailableCommandsCache.class);
    }

}
