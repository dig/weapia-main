package net.sunken.common.inject;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractFacetLoader {

    protected final Set<Facet> pluginFacets;

    public AbstractFacetLoader(Set<Facet> pluginFacets) {
        this.pluginFacets = pluginFacets;
    }

    protected <T> Stream<? extends T> find(Class<T> type) {
        return ((Stream<? extends T>) pluginFacets.stream().filter(type::isInstance));
    }

    protected Set<Enableable> getEnableableFacets() {
        return pluginFacets.stream()
                .filter(facet -> facet instanceof Enableable)
                .map(Enableable.class::cast)
                .collect(Collectors.toSet());
    }

    protected void enableAllFacets() {
        getEnableableFacets().forEach(Enableable::enable);
    }

    protected void enableAllFacets(Predicate<? super Enableable> filter) {
        getEnableableFacets().stream()
                .filter(filter)
                .forEach(Enableable::enable);
    }

    protected void disableAllFacets() {
        getEnableableFacets().forEach(Enableable::disable);
    }

    protected void disableAllFacets(Predicate<? super Enableable> filter) {
        getEnableableFacets().stream()
                .filter(filter)
                .forEach(Enableable::disable);
    }

    public abstract void start();
    public abstract void stop();

}
