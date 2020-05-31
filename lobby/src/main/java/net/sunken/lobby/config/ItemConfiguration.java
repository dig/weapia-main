package net.sunken.lobby.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.List;

@Getter
@ConfigSerializable
public class ItemConfiguration {

    @Setting
    private int slot;
    @Setting
    private Material material;
    @Setting
    private int durability;
    @Setting
    private String displayName;
    @Setting
    private List<String> lore;

}

