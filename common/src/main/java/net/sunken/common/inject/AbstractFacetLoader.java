package net.sunken.common.inject;

import java.util.Set;
import java.util.stream.Stream;

public abstract class AbstractFacetLoader {

    protected final Set<Facet> pluginFacets;

    public AbstractFacetLoader(Set<Facet> pluginFacets) {
        this.pluginFacets = pluginFacets;
    }

    protected <T> Stream<? extends T> find(Class<T> type) {
        return ((Stream<? extends T>) pluginFacets.stream().filter(type::isInstance));
    }

    protected void enableAllFacets() {
        pluginFacets.forEach(facet -> {
            if (facet instanceof Enableable)
                ((Enableable) facet).enable();
        });
    }

    protected void disableAllFacets() {
        pluginFacets.forEach(facet -> {
            if (facet instanceof Enableable)
                ((Enableable) facet).disable();
        });
    }

    public abstract void start();
    public abstract void stop();

}
