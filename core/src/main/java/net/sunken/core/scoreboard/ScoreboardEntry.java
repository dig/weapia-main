package net.sunken.core.scoreboard;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

//--- This needs updating, it's not good.
public class ScoreboardEntry {

    @Getter
    private String key;
    @Getter
    private ScoreboardWrapper scoreboard;
    @Getter
    private String name;
    @Getter
    private Team team;
    @Getter
    private Score score;
    private int value;

    private String origName;
    private int count;

    private ChatColor chatColor;

    public ScoreboardEntry(String key, ScoreboardWrapper spigboard, int value, ChatColor chatColor) {
        this.key = key;
        this.scoreboard = spigboard;
        this.value = value;
        this.count = 0;
        this.chatColor = chatColor;
    }

    public ScoreboardEntry(String key, ScoreboardWrapper spigboard, int value, String origName, int count, ChatColor chatColor) {
        this.key = key;
        this.scoreboard = spigboard;
        this.value = value;
        this.origName = origName;
        this.count = count;
        this.chatColor = chatColor;
    }

    public int getValue() {
        return score != null ? (value = score.getScore()) : value;
    }

    public void setValue(int value) {
        if (!score.isScoreSet()) {
            score.setScore(-1);
        }

        score.setScore(value);
    }

    public void update(String newName) {
        int value = getValue();

        if (origName != null && newName.equals(origName)) {
            for (int i = 0; i < count; i++) {
                newName = ChatColor.RESET + newName;
            }
        } else if (newName.equals(name)) {
            return;
        }

        if (team != null) {
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
        } else {
            create(newName);
        }

        setValue(value);
    }

    public void remove() {
        if (score != null) {
            score.getScoreboard().resetScores(score.getEntry());
        }

        if (team != null) {
            team.unregister();
        }
    }

    private void create(String name) {
        this.name = name;
        remove();

        team = scoreboard.getScoreboard().registerNewTeam("sunken-" + scoreboard.getTeamId());

        score = scoreboard.getObjective().getScore(Strings.repeat(chatColor + "", value + 1));
        team.addEntry(Strings.repeat(chatColor + "", value + 1));

        if (name.length() <= 16) {
            team.setPrefix(name);
            return;
        }

        if (name.length() > 32) {
            name = name.substring(32);
        }

        team.setPrefix(name.substring(0, 16));
        team.setSuffix(name.substring(16));

    }

}
