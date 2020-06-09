package net.sunken.core.scoreboard;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class CustomScoreboard {

    @Getter
    private Scoreboard scoreboard;
    @Getter
    private Objective objective;
    @Getter
    private BiMap<String, CustomScoreboardEntry> entries;

    public CustomScoreboard(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("weapiaobjective", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.entries = HashBiMap.create();
        this.setTitle(title);
    }

    public void setTitle(String title) {
        objective.setDisplayName(title);
    }

    public void createEntry(@NonNull String id, @NonNull String name, int value) {
        CustomScoreboardEntry customScoreboardEntry = new CustomScoreboardEntry(this, id, name, value);
        entries.put(id, customScoreboardEntry);
    }

    public CustomScoreboardEntry getEntry(@NonNull String id) {
        return entries.get(id);
    }

    public void removeAllEntries() {
        entries.values().forEach(CustomScoreboardEntry::remove);
        entries.clear();
    }

    public void add(Player player) {
        player.setScoreboard(this.scoreboard);
    }

}
