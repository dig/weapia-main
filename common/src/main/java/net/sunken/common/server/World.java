package net.sunken.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Getter
@AllArgsConstructor
public enum World {

    NONE ("None",false),
    LOBBY ("Lobby",false),
    GAME_LOBBY ("Game Lobby", false),
    ICERUNNER_PLAINS ("Plains", Game.ICE_RUNNER_SOLO, true);

    private String friendlyName;
    private List<Game> types;
    private boolean map;

    World(String friendlyName, boolean map) {
        this.friendlyName = friendlyName;
        this.types = new ArrayList<>();
        this.map = map;
    }

    World(String friendlyName, Game game, boolean map) {
        this(friendlyName, map);
        this.types.add(game);
    }

    private static Random random = new Random();
    public static World getRandomWorld(Game game) {
        List<World> gameWorlds = new ArrayList<>();

        Arrays.stream(World.values())
                .filter(World::isMap)
                .filter(world -> world.getTypes().contains(game))
                .forEach(world -> gameWorlds.add(world));

        return gameWorlds.get(random.nextInt(gameWorlds.size()));
    }

}