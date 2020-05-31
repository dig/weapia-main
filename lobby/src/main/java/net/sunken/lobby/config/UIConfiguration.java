package net.sunken.lobby.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class UIConfiguration {

    @Setting
    private List<SelectorItemConfiguration> gameSelector;
    @Setting
    private ItemConfiguration lobbySelectorTemplate;

}
