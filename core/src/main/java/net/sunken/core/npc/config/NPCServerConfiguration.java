package net.sunken.core.npc.config;

import lombok.Getter;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class NPCServerConfiguration {

    @Setting
    private Server.Type type;
    @Setting
    private Game game;

}
