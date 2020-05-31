package net.sunken.lobby.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import net.sunken.core.npc.config.NPCConfiguration;
import net.sunken.core.npc.config.SkinConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class LobbyConfiguration {

    @Setting
    private LocationConfiguration spawn;

    @Setting("skin")
    private List<SkinConfiguration> skinConfigurations;

    @Setting("npc")
    private List<NPCConfiguration> npcConfigurations;

}
