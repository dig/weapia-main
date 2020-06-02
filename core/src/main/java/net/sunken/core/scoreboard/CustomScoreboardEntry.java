package net.sunken.core.scoreboard;

import com.google.common.base.Splitter;
import lombok.NonNull;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Iterator;

public class CustomScoreboardEntry {

    private CustomScoreboard customScoreboard;

    private String id;
    private String name;
    private int value;

    private Team team;
    private Score score;

    public CustomScoreboardEntry(@NonNull CustomScoreboard customScoreboard, @NonNull String id, @NonNull String name, int value) {
        this.customScoreboard = customScoreboard;
        this.id = id;
        this.name = name;
        this.value = value;
        this.create();
    }

    private void create() {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        Objective objective = customScoreboard.getObjective();

        if (name.length() <= 16) {
            score = objective.getScore(name);
            score.setScore(value);
        } else {
            team = scoreboard.registerNewTeam("team-" + (id.length() > 11 ? id.substring(0, 11) : id));

            Iterator<String> iterator = Splitter.fixedLength(16).split(name).iterator();
            if (name.length() > 16)
                team.setPrefix(iterator.next());

            String entry = iterator.next();
            score = objective.getScore(entry);
            score.setScore(value);

            if (name.length() > 32)
                team.setSuffix(iterator.next());

            team.addEntry(entry);
        }
    }

    public void update(String newName) {
        if (newName.equals(name)) return;
        name = newName;
        remove();
        create();
    }

    public void remove() {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        if (score != null) scoreboard.resetScores(score.getEntry());
        if (team != null) team.unregister();
    }

}
