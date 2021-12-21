package me.eccentric_nz.ores.ore;

import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.generator.LimitedRegion;

import java.util.Random;

public class OreGenerator {

    private final OreType ore;
    private final World world;
    private final int veinSize;
    private final int tries;
    private final int maxHeight;

    public OreGenerator(OreType ore, World world, int veinSize, int tries, int maxHeight) {
        this.ore = ore;
        this.world = world;
        this.veinSize = veinSize;
        this.tries = tries;
        this.maxHeight = maxHeight;
    }

    public void generate(LimitedRegion region, Random random, int cx, int cz) {
        for (int i = 0; i < tries; i++) {
            int x = cx * 16 + random.nextInt(16);
            int z = cz * 16 + random.nextInt(16);
            int y = random.nextInt(maxHeight);
            for (int j = 0; j < veinSize; j++) {
                Location location = new Location(world, x, y, z);
                if (region.isInRegion(location) && Tag.STONE_ORE_REPLACEABLES.isTagged(region.getType(location))) {
                    region.setBlockData(location, ore.getData());
                }
                x += random.nextInt(3) - 1;
                y += random.nextInt(3) - 1;
                z += random.nextInt(3) - 1;
            }
        }
    }
}
