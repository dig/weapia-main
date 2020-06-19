package net.sunken.master.instance.config;

import lombok.Getter;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Map;

@Getter
@ConfigSerializable
public class InstanceConfiguration {

    @Setting
    private Map<Server.Type, InstanceGameConfiguration> types;

    @Setting
    private Map<Game, InstanceGameConfiguration> games;

}
