package net.sunken.master.instance.config;

import lombok.Getter;
import net.sunken.common.server.World;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class InstanceGameConfiguration {

    @Setting
    private InstanceGameInfraConfiguration infrastructure;

    @Setting
    private InstanceGamePoolConfiguration pool;

    @Setting
    private List<World> worlds;

}
