package net.sunken.core.player;

import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.core.Constants;
import net.sunken.core.engine.state.impl.BasePlayerState;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import net.sunken.core.util.TabList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public abstract class CorePlayer extends AbstractPlayer {

    @Getter
    protected BasePlayerState state;
    protected ScoreboardRegistry scoreboardRegistry;

    public CorePlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry) {
        super(uuid, username);
        this.state = null;
        this.scoreboardRegistry = scoreboardRegistry;
    }

    public void setup(@NonNull Player player) {
        setTabList(player);
        setNametagAndTabList(player);
    }

    public void destroy(@NonNull Player player) {
    }

    public void setTabList(@NonNull Player player) {
        TabList.send(player, ChatColor.translateAlternateColorCodes('&', Constants.TAB_TOP), ChatColor.translateAlternateColorCodes('&', Constants.TAB_BOTTOM));
    }

    public void setNametagAndTabList(@NonNull Player player) {
        switch (rank) {
            case PLAYER:
                player.setPlayerListName(ChatColor.valueOf(rank.getColour()) + player.getName());
                scoreboardRegistry.changeName(this, "", "", ChatColor.valueOf(rank.getColour()), rank.getOrder());
                break;
            default:
                player.setPlayerListName(ChatColor.valueOf(rank.getColour()) + "[" + rank.getFriendlyName().toUpperCase() + "] " + player.getName());
                scoreboardRegistry.changeName(this, ChatColor.valueOf(rank.getColour()) + "[" + rank.getFriendlyName().toUpperCase() + "] ", "", ChatColor.valueOf(rank.getColour()), rank.getOrder());
        }
    }

    public Optional<? extends Player> toPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    public void setState(BasePlayerState newState) {
        if (state != null) {
            state.stop(this, newState);
        }

        newState.start(this, state);
        state = newState;
    }
}
