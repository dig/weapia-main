package net.sunken.core.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.database.MongoConnection;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.util.Symbol;
import net.sunken.core.Constants;
import net.sunken.core.PluginInform;
import net.sunken.core.engine.state.impl.BasePlayerState;
import net.sunken.core.scoreboard.CustomScoreboard;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import net.sunken.core.util.ActionBar;
import net.sunken.core.util.TabList;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

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
        setScoreboard(player);
        ActionBar.sendMessage(player, "");
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

    public void setScoreboard(@NonNull Player player) {
        CustomScoreboard customScoreboard = new CustomScoreboard(ChatColor.AQUA + "" + ChatColor.BOLD + "WEAPIA");

        if (!setupScoreboard(customScoreboard)) return;
        customScoreboard.createEntry("ServerID", ChatColor.GRAY + pluginInform.getServer().getId(), 1);
        customScoreboard.createEntry("URL", ChatColor.LIGHT_PURPLE + "play.weapia.com", 0);

        customScoreboard.add(player);
        scoreboardRegistry.register(player.getUniqueId().toString(), customScoreboard);
    }

    protected abstract boolean setupScoreboard(@NonNull CustomScoreboard scoreboard);

    public void save(@NonNull MongoConnection mongoConnection, @NonNull Player player) {
        MongoCollection<Document> collection = mongoConnection.getCollection(DatabaseHelper.DATABASE_MAIN, DatabaseHelper.COLLECTION_PLAYER);
        collection.updateOne(eq(DatabaseHelper.PLAYER_UUID_KEY, uuid.toString()),
                new Document("$set", toDocument(player)), new UpdateOptions().upsert(true));
    }

    public Document toDocument(@NonNull Player player) {
        return toDocument();
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
