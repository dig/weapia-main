package net.sunken.master.instance.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class InstanceGamePoolConfiguration {

    @Setting
    private int min;

    @Setting
    private int expire;

}
