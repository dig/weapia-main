package net.sunken.core.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public class CustomNameDetail {

    private final String prefix;
    private final String suffix;
    private final ChatColor colour;
    private final int order;

}
