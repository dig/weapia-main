package net.sunken.core.engine.state.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class WaitingConfiguration {

    @Setting
    private int requiredPlayers;

    @Setting
    private long countdown;

    @Setting
    private List<TimerConfiguration> shortenTimer;

    @Setting("spawn")
    private LocationConfiguration locationConfiguration;

    @Setting
    private long spawnTeleportBackRadius;

}
