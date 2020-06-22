package net.sunken.core.player;

import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.util.Symbol;
import net.sunken.core.Constants;
import net.sunken.core.PluginInform;
import net.sunken.core.engine.state.impl.BasePlayerState;
import net.sunken.core.scoreboard.CustomScoreboard;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import net.sunken.core.util.TabList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class CorePlayer extends AbstractPlayer {

    @Getter
    protected BasePlayerState state;
    protected ScoreboardRegistry scoreboardRegistry;
    protected PluginInform pluginInform;

    public CorePlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry, PluginInform pluginInform) {
        super(uuid, username);
        this.state = null;
        this.scoreboardRegistry = scoreboardRegistry;
        this.pluginInform = pluginInform;
    }

    public void setup(@NonNull Player player) {
        setTabList(player);
        setNametagAndTabList(player);
    }

    public void destroy(@NonNull Player player) {
        scoreboardRegistry.unregister(uuid.toString());
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

    protected void setScoreboard(@NonNull Player player, Consumer<CustomScoreboard> consumer) {
        CustomScoreboard customScoreboard = new CustomScoreboard(ChatColor.AQUA + "" + ChatColor.BOLD + "WEAPIA");

        consumer.accept(customScoreboard);
        customScoreboard.createEntry("ServerID", ChatColor.GRAY + pluginInform.getServer().getId(), 1);
        customScoreboard.createEntry("URL", ChatColor.LIGHT_PURPLE + "play.weapia.com", 0);

        customScoreboard.add(player);
        scoreboardRegistry.register(player.getUniqueId().toString(), customScoreboard);
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
