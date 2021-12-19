package me.eccentric_nz.ores.ore;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrePopulator extends BlockPopulator {

    private final List<OreGenerator> ores = new ArrayList<OreGenerator>();
    private World world;

    public OrePopulator(World world) {
        // OreGenerator(Ore ore, int veinSize, int tries, int maxHeight)
        ores.add(new OreGenerator(Ore.ALUMINIUM, world, 9, 20, 64));
        ores.add(new OreGenerator(Ore.URANIUM, world, 9, 20, 64));
        ores.add(new OreGenerator(Ore.LEAD, world, 9, 20, 64));
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int x, int z, LimitedRegion limitedRegion) {
        for (OreGenerator ore : ores) {
            ore.generate(limitedRegion, random, x, z);
        }
    }
}
