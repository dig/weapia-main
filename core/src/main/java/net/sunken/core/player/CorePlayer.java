package net.sunken.core.player;

import lombok.Getter;
import lombok.Setter;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.core.Constants;
import net.sunken.core.engine.state.impl.BasePlayerState;
import net.sunken.core.scoreboard.ScoreboardManager;
import net.sunken.core.scoreboard.ScoreboardWrapper;
import net.sunken.core.util.ColourUtil;
import net.sunken.core.util.TabListUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public abstract class CorePlayer extends AbstractPlayer {

    protected ScoreboardManager scoreboardManager;
    @Setter
    protected ScoreboardWrapper scoreboardWrapper;

    @Getter
    protected BasePlayerState state;

    public CorePlayer(UUID uuid, String username, ScoreboardManager scoreboardManager) {
        super(uuid, username);
        this.scoreboardManager = scoreboardManager;
        this.state = null;
    }

    public ScoreboardWrapper getScoreboardWrapper() {
        return scoreboardWrapper;
    }

    @Override
    public void setup() {
        Optional<? extends Player> playerOptional = toPlayer();
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();

            TabListUtil.send(player, ChatColor.translateAlternateColorCodes('&', Constants.TAB_TOP), ChatColor.translateAlternateColorCodes('&', Constants.TAB_BOTTOM));

            //--- Player list & nametag
            switch (rank) {
                case PLAYER:
                    player.setPlayerListName(ColourUtil.fromColourCode(rank.getColourCode()) + player.getName());
                    scoreboardManager.changePlayerName(player, ColourUtil.fromColourCode(rank.getColourCode()) + "", "");
                    break;
                default:
                    player.setPlayerListName(ColourUtil.fromColourCode(rank.getColourCode()) + "[" + rank.getFriendlyName().toUpperCase() + "] " + player.getName());
                    scoreboardManager.changePlayerName(player, ColourUtil.fromColourCode(rank.getColourCode()) + "[" + rank.getFriendlyName().toUpperCase() + "] ", "");
            }
        }
    }

    @Override
    public void destroy() { scoreboardWrapper.getEntries().values().forEach(scoreboardEntry -> scoreboardEntry.remove()); }

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
