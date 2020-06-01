package net.sunken.core.team.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.ChatColor;

import java.util.List;

@Getter
@ConfigSerializable
public class TeamConfiguration {

    @Setting
    private List<TeamSingleConfiguration> teamsAvailable;

    @Setting
    private int playersPerTeam;

}
