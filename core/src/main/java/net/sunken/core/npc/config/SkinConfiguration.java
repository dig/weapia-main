package net.sunken.core.npc.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class SkinConfiguration {

    @Setting
    private String texture;

    @Setting
    private String signature;

}
