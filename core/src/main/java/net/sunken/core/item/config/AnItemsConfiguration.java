package net.sunken.core.item.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class AnItemsConfiguration {

    @Setting
    private List<AnItemConfiguration> items;

}
