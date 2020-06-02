package net.sunken.core.scoreboard;

import lombok.NonNull;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
        this.setup();
    }

    private void setup() {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        Objective objective = customScoreboard.getObjective();

        team = scoreboard.registerNewTeam("team-" + id);
        score = objective.getScore("score-" + id);
        team.addEntry("score-" + id);

        if (name.length() <= 16) {
            team.setPrefix(name);
        } else if (name.length() > 32) {
            name = name.substring(32);
        }

        team.setPrefix(name.substring(0, 16));
        team.setSuffix(name.substring(16));
    }

    public void update(String newName) {
        if (newName.equals(name)) return;
        if (newName.length() <= 16) {
            team.setPrefix(newName);
            team.setSuffix("");
        } else {
            if (newName.length() > 32) {
                newName = newName.substring(32);
            }

            team.setPrefix(newName.substring(0, 16));
            team.setSuffix(newName.substring(16));
        }

        name = newName;
    }

    public void remove() {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        scoreboard.resetScores(score.getEntry());
        team.unregister();
    }

}
