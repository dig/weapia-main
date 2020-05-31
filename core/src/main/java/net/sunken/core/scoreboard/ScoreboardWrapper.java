package net.sunken.core.scoreboard;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

//--- This needs updating, it's not good.
public class ScoreboardWrapper {

    private ScoreboardManager scoreboardManager;

    @Getter
    private Scoreboard scoreboard;
    @Getter
    private Objective objective;
    private BiMap<String, ScoreboardEntry> entries;

    private int teamId;

    public ScoreboardWrapper(String title, ScoreboardManager scoreboardManager) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("sunkenobjective", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        setTitle(title);

        this.entries = HashBiMap.create();
        this.teamId = 1;

        this.scoreboardManager = scoreboardManager;
    }

    public void setTitle(String title) {
        objective.setDisplayName(title);
    }

    public BiMap<String, ScoreboardEntry> getEntries() {
        return HashBiMap.create(entries);
    }

    public ScoreboardEntry getEntry(String key) {
        return entries.get(key);
    }

    public ScoreboardEntry add(String key, String name, int value) {
        return add(key, name, value, false, ChatColor.WHITE);
    }

    public ScoreboardEntry add(String key, String name, int value, ChatColor chatColor) {
        return add(key, name, value, false, chatColor);
    }

    public ScoreboardEntry add(String key, String name, int value, boolean overwrite, ChatColor chatColor) {
        if (key == null && !contains(name)) {
            throw new IllegalArgumentException("Entry could not be found with the supplied name and no key was supplied");
        }

        if (overwrite && contains(name)) {
            ScoreboardEntry entry = getEntryByName(name);

            if (key != null && entries.get(key) != entry) {
                throw new IllegalArgumentException("Supplied key references a different score than the one to be overwritten");
            }

            entry.setValue(value);
            return entry;
        }

        if (entries.get(key) != null) {
            throw new IllegalArgumentException("Score already exists with that key");
        }

        int count = 0;
        String origName = name;

        if (!overwrite) {
            Map<Integer, String> created = create(name);
            for (Map.Entry<Integer, String> entry : created.entrySet()) {
                count = entry.getKey();
                name = entry.getValue();
            }
        }

        ScoreboardEntry entry = new ScoreboardEntry(key, this, value, origName, count, chatColor);
        entry.update(name);
        entries.put(key, entry);

        return entry;
    }

    public void remove(String key) {
        remove(getEntry(key));
    }

    public void remove(ScoreboardEntry entry) {
        if (entry.getScoreboard() != this) {
            throw new IllegalArgumentException("Supplied entry does not belong to this Scoreboard");
        }

        String key = entries.inverse().get(entry);
        if (key != null) {
            entries.remove(key);
        }

        entry.remove();
    }

    public void removeAllEntries() {
        for (ScoreboardEntry entry : entries.values()) {
            entry.remove();
        }

        entries.clear();
    }

    private Map<Integer, String> create(String name) {
        int count = 0;
        while (contains(name)) {
            name = ChatColor.RESET + name;
            count++;
        }

        if (name.length() > 48) {
            name = name.substring(0, 47);
        }

        if (contains(name)) {
            throw new IllegalArgumentException("Could not find a suitable replacement name for '" + name + "'");
        }

        Map<Integer, String> created = new HashMap<>();
        created.put(count, name);

        return created;
    }

    public int getTeamId() {
        return teamId++;
    }

    public ScoreboardEntry getEntryByName(String name) {
        for (ScoreboardEntry entry : entries.values()) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }

        return null;
    }

    public boolean contains(String name) {
        for (ScoreboardEntry entry : entries.values()) {
            if (entry.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void add(Player player) {
        scoreboardManager.load(scoreboard);
        player.setScoreboard(scoreboard);
    }

}
