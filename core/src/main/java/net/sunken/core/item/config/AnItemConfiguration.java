package net.sunken.core.item.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@ConfigSerializable
public class AnItemConfiguration {

    @Setting
    private String id;

    @Setting
    private boolean stack;

    @Setting
    private Material material;

    @Setting
    private String displayName;

    @Setting
    private List<String> lore;

    @Setting
    private List<ItemAttributeConfiguration> attributes;

    @Setting
    @Nullable
    private String itemClass;

}
