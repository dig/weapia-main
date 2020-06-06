package net.sunken.core.engine.state.impl;

import com.google.inject.Inject;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import net.sunken.core.team.impl.Team;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public abstract class BaseTeamState {

    @Inject
    private ScoreboardRegistry scoreboardRegistry;
    @Inject
    private PlayerManager playerManager;

    //--- Called on state start.
    public abstract void start(Team team, BaseTeamState previous);

    //--- Called on state stop, before switching.
    public abstract void stop(Team team, BaseTeamState next);

    //--- Called when a player joins the team.
    public void onJoin(Team team, UUID uuid) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(uuid);
        if (abstractPlayerOptional.isPresent()) {
            CorePlayer corePlayer = (CorePlayer) abstractPlayerOptional.get();

            Optional<? extends Player> playerOptional = corePlayer.toPlayer();
            if (playerOptional.isPresent()) {
                Player player = playerOptional.get();
                player.setPlayerListName(team.getColour() + "[" + team.getDisplayName() + "] " + player.getName());
            }

            scoreboardRegistry.changeName(corePlayer, team.getColour() + "[" + team.getDisplayName() + "] ", "", team.getColour(), team.getColour().ordinal());
        }
    }

    //--- Called when a player leaves the team.
    public void onQuit(Team team, UUID uuid) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(uuid);
        if (abstractPlayerOptional.isPresent()) {
            CorePlayer corePlayer = (CorePlayer) abstractPlayerOptional.get();
            corePlayer.setNametagAndTabList();
        }
    }

}
