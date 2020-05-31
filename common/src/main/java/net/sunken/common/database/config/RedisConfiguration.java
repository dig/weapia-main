package net.sunken.common.database.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class RedisConfiguration {

    @Setting
    private String host;

    @Setting
    private int port;

    @Setting
    private String password;

}
