package net.sunken.master.instance.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class InstanceGameInfraConfiguration {

    @Setting
    private String production;

    @Setting
    private String development;

}
