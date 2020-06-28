package net.sunken.common.inject;

import com.google.inject.*;
import com.google.inject.multibindings.*;

public class FacetBinder {

    private final Multibinder<Facet> multibinder;

    public FacetBinder(Binder binder) {
        this.multibinder = Multibinder.newSetBinder(binder, Facet.class);
    }

    public void addBinding(Class<? extends Facet> t) {
        multibinder.addBinding().to(t);
    }
}
