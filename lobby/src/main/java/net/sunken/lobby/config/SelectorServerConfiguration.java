package net.sunken.lobby.config;

import lombok.Getter;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class SelectorServerConfiguration {

    @Setting
    private Server.Type type;
    @Setting
    private Game game;

}
