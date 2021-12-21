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
}