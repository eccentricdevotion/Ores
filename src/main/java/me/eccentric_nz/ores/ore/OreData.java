package me.eccentric_nz.ores.ore;

import org.bukkit.Bukkit;
import org.bukkit.block.data.MultipleFacing;

public class OreData {

    public static final MultipleFacing bauxiteMushroom = (MultipleFacing) Bukkit.createBlockData("minecraft:brown_mushroom_block[down=true,east=true,north=true,south=false,up=false,west=false]");
    public static final MultipleFacing leadMushroom = (MultipleFacing) Bukkit.createBlockData("minecraft:brown_mushroom_block[down=true,east=true,north=true,south=false,up=false,west=true]");
    public static final MultipleFacing uraniumMushroom = (MultipleFacing) Bukkit.createBlockData("minecraft:brown_mushroom_block[down=true,east=true,north=true,south=false,up=true,west=false]");
    public static final MultipleFacing collectorNorthMushroom = (MultipleFacing) Bukkit.createBlockData("minecraft:brown_mushroom_block[down=true,east=true,north=true,south=false,up=true,west=true]");
    public static final MultipleFacing collectorEastMushroom = (MultipleFacing) Bukkit.createBlockData("minecraft:brown_mushroom_block[down=true,east=true,north=true,south=true,up=false,west=false]");
    public static final MultipleFacing collectorSouthMushroom = (MultipleFacing) Bukkit.createBlockData("minecraft:brown_mushroom_block[down=true,east=true,north=true,south=true,up=false,west=true]");
    public static final MultipleFacing collectorWestMushroom = (MultipleFacing) Bukkit.createBlockData("minecraft:brown_mushroom_block[down=true,east=true,north=true,south=true,up=true,west=false]");
    public static final MultipleFacing nuclearGeneratorMushroom = (MultipleFacing) Bukkit.createBlockData("minecraft:brown_mushroom_block[down=true,east=true,north=false,south=true,up=true,west=true]");

    public static boolean isOreMushroom(MultipleFacing data) {
        return data.matches(bauxiteMushroom) || data.matches(leadMushroom) || data.matches(uraniumMushroom);
    }

    public static boolean isCollectorMushroom(MultipleFacing data) {
        return data.matches(collectorEastMushroom) || data.matches(collectorNorthMushroom) || data.matches(collectorWestMushroom) || data.matches(collectorSouthMushroom);
    }

    public static boolean isNuclearMushroom(MultipleFacing data) {
        return data.matches(nuclearGeneratorMushroom);
    }
}