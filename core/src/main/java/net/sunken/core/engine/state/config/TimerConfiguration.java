package net.sunken.core.engine.state.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class TimerConfiguration {

    @Setting
    private int required;

    @Setting
    private long timeLeft;

    @Setting
    private String message;

}
