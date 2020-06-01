package net.sunken.core.team.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.ChatColor;

@Getter
@ConfigSerializable
public class TeamSingleConfiguration {

    @Setting
    private ChatColor colour;
    @Setting
    private String displayName;
    @Setting
    private int maxPlayers;

}
