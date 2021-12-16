package me.eccentric_nz.ores;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.generator.LimitedRegion;

import java.util.Random;

public class OreGenerator {

    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_LENGTH = 16;
    public static final int CHUNK_HEIGHT = 384;

    private final Ore ore;
    private final int veinSize;
    private final int tries;
    private final int maxHeight;

    public OreGenerator(Ore ore, int veinSize, int tries, int maxHeight) {
        this.ore = ore;
        this.veinSize = veinSize;
        this.tries = tries;
        this.maxHeight = maxHeight;
    }

    public void generate(LimitedRegion region, Random random) {
        for (int i = 0; i < tries; i++) {
            int x = random.nextInt(CHUNK_WIDTH);
            int z = random.nextInt(CHUNK_LENGTH);
            int y = random.nextInt(maxHeight);
            Location location = new Location(null, x, y, z);
            for (int j = 0; j < veinSize; j++) {
                if (x >= 0 && x < CHUNK_WIDTH && z >= 0 && z < CHUNK_LENGTH && y >= 0 && y < CHUNK_HEIGHT && region.getType(location) == Material.STONE) {
                    new OreFrame(ore, location);
                }
                x += random.nextInt(3) - 1;
                y += random.nextInt(3) - 1;
                z += random.nextInt(3) - 1;
            }
        }
    }
}
