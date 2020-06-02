package net.sunken.core.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public class CustomNameDetail {

    private String prefix;
    private String suffix;
    private ChatColor colour;

}
