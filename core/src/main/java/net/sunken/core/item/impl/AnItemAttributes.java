package net.sunken.core.item.impl;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnItemAttributes {

    private final Map<String, Object> attributes = new HashMap<>();

    public String getString(String key) {
        return (String) attributes.get(key);
    }

    public int getInt(String key) {
        return (int) attributes.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) attributes.get(key);
    }

    public double getDouble(String key) {
        return (double) attributes.get(key);
    }

    public Object get(String key) {
        return attributes.get(key);
    }

    public void set(String key, Object value) {
        attributes.put(key, value);
    }

    public Set<String> getKeys() {
        return attributes.keySet();
    }

}
