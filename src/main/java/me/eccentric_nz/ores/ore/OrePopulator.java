package me.eccentric_nz.ores.ore;

import me.eccentric_nz.ores.Ores;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrePopulator extends BlockPopulator {

    private final List<OreGenerator> ores = new ArrayList<OreGenerator>();
    private final Ores plugin;

    public OrePopulator(Ores plugin, World world) {
        this.plugin = plugin;
        FileConfiguration config = this.plugin.getConfig();
        // OreGenerator(Ore ore, int veinSize, int tries, int minHeight, int maxHeight)
        ores.add(new OreGenerator(OreType.ALUMINIUM, world, config.getInt("ores.aluminium.vein_size"), config.getInt("ores.aluminium.tries"), config.getInt("ores.aluminium.min_height"), config.getInt("ores.aluminium.max_height")));
        ores.add(new OreGenerator(OreType.URANIUM, world, config.getInt("ores.uranium.vein_size"), config.getInt("ores.uranium.tries"), config.getInt("ores.uranium.min_height"), config.getInt("ores.uranium.max_height")));
        ores.add(new OreGenerator(OreType.LEAD, world, config.getInt("ores.lead.vein_size"), config.getInt("ores.lead.tries"), config.getInt("ores.lead.min_height"), config.getInt("ores.lead.max_height")));
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int x, int z, LimitedRegion limitedRegion) {
        for (OreGenerator ore : ores) {
            ore.generate(limitedRegion, random, x, z);
        }
    }
}
