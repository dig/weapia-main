package net.sunken.core.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
@ConfigSerializable
public class LocationConfiguration {

    @Setting
    private String world;

    @Setting
    private double x;
    @Setting
    private double y;
    @Setting
    private double z;

    @Setting
    private float yaw;
    @Setting
    private float pitch;

    public Location toLocation() {
        return new Location(
            Bukkit.getWorld(world),
            x,
            y,
            z,
            yaw,
            pitch
        );
    }

}
