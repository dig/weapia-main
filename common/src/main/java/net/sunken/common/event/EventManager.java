package net.sunken.common.event;

import com.google.common.collect.Sets;
import com.google.inject.Singleton;
import lombok.extern.java.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

@Log
@Singleton
public class EventManager {

    private Set<SunkenListener> allListeners;

    public EventManager() {
        allListeners = Sets.newLinkedHashSet();
    }

    public <T extends SunkenListener> void register(T listener) {
        allListeners.add(listener);
    }

    public <T extends SunkenListener> void unregister(T listener) { allListeners.remove(listener); }

    public <T extends SunkenEvent> void callEvent(T event) {
        for (SunkenListener listener : allListeners) {
            Class<? extends SunkenListener> clazz = listener.getClass();
            Method[] declaredMethods = clazz.getDeclaredMethods();

            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(ListensToEvent.class)) {
                    Parameter[] parameters = method.getParameters();

                    if (parameters.length == 1) {
                        for (Parameter parameter : parameters) {
                            if (parameter.getType().getCanonicalName().equals(event.getClass().getCanonicalName())) {
                                try {
                                    method.invoke(listener, event);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
