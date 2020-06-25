package net.sunken.master.queue;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.FacetBinder;

public class QueueModule extends AbstractModule {

    @Override
    public void configure() {
        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(QueueManager.class);
    }
}
