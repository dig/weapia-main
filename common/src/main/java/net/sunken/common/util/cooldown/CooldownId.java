package net.sunken.common.util.cooldown;

import java.util.*;

final class CooldownId {

    private final String key;
    private final UUID uuid;

    private CooldownId(String key, UUID uuid) {
        this.key = key;
        this.uuid = uuid;
    }

    static CooldownId of(String key, UUID uuid) {
        return new CooldownId(key, uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CooldownId that = (CooldownId) o;
        return Objects.equals(key, that.key) && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, uuid);
    }
}
