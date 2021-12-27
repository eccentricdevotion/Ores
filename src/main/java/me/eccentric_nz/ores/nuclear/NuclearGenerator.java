package me.eccentric_nz.ores.nuclear;

import me.eccentric_nz.ores.mOre;
import me.eccentric_nz.ores.ore.OreData;
import me.eccentric_nz.ores.pipe.PipeCoords;
import me.eccentric_nz.ores.pipe.PipePath;
import me.eccentric_nz.ores.pipe.PipeShape;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class NuclearGenerator implements Runnable {

    private final List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private final Levelled water = (Levelled) Bukkit.createBlockData(Material.WATER);

    public NuclearGenerator() {
        water.setLevel(15);
    }

    public NuclearData getData(Block block) {
        NuclearData data = new NuclearData();
        // is it powered?
        int amount = NuclearInventory.getAmount(block);
        data.setPowered(amount > 0);
        // find the collector
        for (BlockFace face : faces) {
            Block attached = block.getRelative(face);
            if (attached.getType().equals(Material.BROWN_MUSHROOM_BLOCK)) {
                MultipleFacing mushroom = (MultipleFacing) attached.getBlockData();
                if (OreData.isCollectorMushroom(mushroom)) {
                    // set the facing direction
                    data.setFacing(face);
                    // is there a hopper or water cauldron?
                    Block above = attached.getRelative(BlockFace.UP);
                    // find the hopper
                    if (above.getType().equals(Material.HOPPER)) {
                        data.setHopper((Hopper) above.getState());
                    }
                    // find the cauldron
                    if (above.getType().equals(Material.WATER_CAULDRON) || above.getType().equals(Material.CAULDRON)) {
                        data.setWater(above);
                    }
                    // get the pipe shape and location
                    BlockFace collectorFacing = getCollectorFacing(mushroom);
                    Block pipe = attached.getRelative(collectorFacing);
                    PipeShape shape = PipeShape.get(pipe.getWorld(), new PipeCoords(pipe.getX(), pipe.getY(), pipe.getZ()));
                    if (shape != null) {
                        data.setShape(shape);
                        data.setStart(pipe.getLocation());
                        // get length and chested
                        PipePath.PipeData pipeData = new PipePath().getExit(pipe.getLocation(), shape, face);
                        data.setLength(pipeData.getLength());
                        data.setEnd(pipeData.getExit());
                        data.setReceiver(pipeData.getExit().getBlock().getState() instanceof Container);
                        break;
                    }
                }
            }
        }
        return data;
    }

    private BlockFace getCollectorFacing(MultipleFacing mushroom) {
        if (mushroom.matches(OreData.collectorNorthMushroom)) {
            return BlockFace.NORTH;
        }
        if (mushroom.matches(OreData.collectorEastMushroom)) {
            return BlockFace.EAST;
        }
        if (mushroom.matches(OreData.collectorSouthMushroom)) {
            return BlockFace.SOUTH;
        }
        return BlockFace.WEST; // OreData.collectorWestMushroom
    }

    @Override
    public void run() {
        for (Block generator : NuclearStorage.getBlocks()) {
            // get nuclear data
            NuclearData data = getData(generator);
            if (data.isPowered() && data.hasSource() && data.getShape() != null) {
                if (data.getHopper() != null) {
                    ItemStack toMove = null;
                    // get an item from the hopper and send it to the exit
                    for (int i = data.getHopper().getInventory().getSize() - 1; i >= 0; i--) {
                        ItemStack item = data.getHopper().getInventory().getItem(i);
                        if (item != null) {
                            toMove = item.clone();
                            toMove.setAmount(1);
                            item.setAmount(item.getAmount() - 1);
                            break;
                        }
                    }
                    if (toMove != null) {
                        ItemStack exitItem = toMove;
                        if (data.hasReceiver()) {
                            // put it in the container
                            // TODO probably need to check there is room in container / chunk is loaded etc...
                            Bukkit.getScheduler().scheduleSyncDelayedTask(mOre.getPlugin(), () -> {
                                Container container = (Container) data.getEnd().getBlock().getState();
                                container.getInventory().addItem(exitItem);
                            }, data.getLength() * mOre.getPlugin().getConfig().getLong("nuclear.pipe_ticks"));
                        } else {
                            // spawn item at end pipe location
                            Bukkit.getScheduler().scheduleSyncDelayedTask(mOre.getPlugin(), () -> {
                                Location exit = data.getEnd().add(0.5, 0, 0.5);
                                data.getEnd().getWorld().dropItem(exit, exitItem);
                            }, data.getLength() * mOre.getPlugin().getConfig().getLong("nuclear.pipe_ticks"));
                        }
                    }
                }
                if (data.getWater() != null) {
                    boolean hasLevels = data.getWater().getType().equals(Material.WATER_CAULDRON);
                    if (hasLevels) {
                        Levelled levelled = (Levelled) data.getWater().getBlockData();
                        hasLevels = levelled.getLevel() > 0;
                    }
                    Material material = data.getEnd().getBlock().getType();
                    if (hasLevels) {
                        if (!material.equals(Material.WATER)) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(mOre.getPlugin(), () -> {
                                data.getEnd().getBlock().setType(Material.WATER);
                            }, data.getLength() * mOre.getPlugin().getConfig().getLong("nuclear.pipe_ticks"));
                        }
                    } else if (material.equals(Material.WATER)) {
                        // remove water
                        Bukkit.getScheduler().scheduleSyncDelayedTask(mOre.getPlugin(), () -> {
                            data.getEnd().getBlock().setType(Material.AIR);
                        }, data.getLength() * mOre.getPlugin().getConfig().getLong("nuclear.pipe_ticks"));
                    }
                }
            }
        }
    }
}
