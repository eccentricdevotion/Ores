package me.eccentric_nz.ores.nuclear;

import me.eccentric_nz.ores.ore.OreData;
import me.eccentric_nz.ores.pipe.PipeCoords;
import me.eccentric_nz.ores.pipe.PipeShape;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.MultipleFacing;

import java.util.Arrays;
import java.util.List;

public class NuclearGenerator implements Runnable {

    private final List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

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
                        data.setHopper((Hopper) block.getState());
                    }
                    // find the cauldron
                    if (above.getType().equals(Material.WATER_CAULDRON)) {
                        data.setWater(above);
                        break;
                    }
                    // get the pipe shape and location
                    Block pipe = attached.getRelative(getCollectorFacing(mushroom));
                    PipeShape shape = PipeShape.get(pipe.getWorld(), new PipeCoords(pipe.getX(), pipe.getY(), pipe.getZ()));
                    if (shape != null) {
                        data.setShape(shape);
                        data.setStart(pipe.getLocation());
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
            if (data.isPowered() && data.hasSource()) {
                
            }
        }
    }

    public class NuclearData {

        private PipeShape shape;
        private Location start;
        private BlockFace facing;
        private Hopper hopper;
        private Block water;
        private Block generator;
        private boolean powered;
        private boolean source;

        public PipeShape getShape() {
            return shape;
        }

        public void setShape(PipeShape shape) {
            this.shape = shape;
        }

        public Location getStart() {
            return start;
        }

        public void setStart(Location start) {
            this.start = start;
        }

        public BlockFace getFacing() {
            return facing;
        }

        public void setFacing(BlockFace facing) {
            this.facing = facing;
        }

        public Hopper getHopper() {
            return hopper;
        }

        public void setHopper(Hopper hopper) {
            this.hopper = hopper;
        }

        public Block getWater() {
            return water;
        }

        public void setWater(Block water) {
            this.water = water;
        }

        public Block getGenerator() {
            return generator;
        }

        public void setGenerator(Block generator) {
            this.generator = generator;
        }

        public boolean isPowered() {
            return powered;
        }

        public void setPowered(boolean powered) {
            this.powered = powered;
        }

        public boolean hasSource() {
            return hopper != null || water != null;
        }
    }
}
