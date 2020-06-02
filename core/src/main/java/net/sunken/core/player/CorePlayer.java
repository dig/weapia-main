package net.sunken.core.player;

import lombok.Getter;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.core.Constants;
import net.sunken.core.engine.state.impl.BasePlayerState;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import net.sunken.core.util.ColourUtil;
import net.sunken.core.util.TabListUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public abstract class CorePlayer extends AbstractPlayer {

    protected ScoreboardRegistry scoreboardRegistry;

    @Getter
    protected BasePlayerState state;

    public CorePlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry) {
        super(uuid, username);
        this.state = null;
        this.scoreboardRegistry = scoreboardRegistry;
    }

    @Override
    public void setup() {
        setTabList();
        setNametagAndTabList();
    }

    public void setTabList() {
        Optional<? extends Player> playerOptional = toPlayer();
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            TabListUtil.send(player, ChatColor.translateAlternateColorCodes('&', Constants.TAB_TOP), ChatColor.translateAlternateColorCodes('&', Constants.TAB_BOTTOM));
        }
    }

    public void setNametagAndTabList() {
        Optional<? extends Player> playerOptional = toPlayer();
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            switch (rank) {
                case PLAYER:
                    player.setPlayerListName(ColourUtil.fromColourCode(rank.getColourCode()) + player.getName());
                    scoreboardRegistry.changeName(this.username, "", "", ColourUtil.fromColourCode(rank.getColourCode()));
                    break;
                default:
                    player.setPlayerListName(ColourUtil.fromColourCode(rank.getColourCode()) + "[" + rank.getFriendlyName().toUpperCase() + "] " + player.getName());
                    scoreboardRegistry.changeName(this.username, ColourUtil.fromColourCode(rank.getColourCode()) + "[" + rank.getFriendlyName().toUpperCase() + "] ", "", ColourUtil.fromColourCode(rank.getColourCode()));
            }
        }
    }

    @Override
    public void destroy() {
    }

    public Optional<? extends Player> toPlayer() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getUniqueId().equals(uuid))
                .findFirst();
    }

    public void setState(BasePlayerState newState) {
        if (state != null) {
            state.stop(newState);
        }

        newState.start(state);
        state = newState;
    }

    public boolean hasState() {
        return state != null;
    }

}
