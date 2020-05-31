package com.minevasion.world;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {

    public List<BlockPopulator> getDefaultPopulators(final World world) {
        return new ArrayList<>();
    }

    public ChunkGenerator.ChunkData generateChunkData(final World world, final Random random, final int ChunkX, final int ChunkZ, final ChunkGenerator.BiomeGrid biome) {
        final ChunkGenerator.ChunkData data = this.createChunkData(world);
        return data;
    }

}
