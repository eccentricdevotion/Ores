package me.eccentric_nz.ores.pipe;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class PipePath {

    public PipeData getExit(Location start, PipeShape shape, BlockFace facing) {
        PipeCoords coords = new PipeCoords(start.getBlockX(), start.getBlockY(), start.getBlockZ());
        int length = 0;
        while (shape != null) {
            length++;
            PipeCoords tmp = new PipeCoords(coords.getX(), coords.getY(), coords.getZ());
            // get the pipe at the location
            switch (shape) {
                case NORTH_EAST -> {
                    if (facing == BlockFace.SOUTH) {
                        facing = BlockFace.EAST;
                    } else { // WEST
                        facing = BlockFace.NORTH;
                    }
                    coords = tmp.relative(facing);
                }
                case NORTH_WEST -> {
                    if (facing == BlockFace.SOUTH) {
                        facing = BlockFace.WEST;
                    } else { // EAST
                        facing = BlockFace.NORTH;
                    }
                    coords = tmp.relative(facing);
                }
                case SOUTH_EAST -> {
                    if (facing == BlockFace.NORTH) {
                        facing = BlockFace.EAST;
                    } else { // WEST
                        facing = BlockFace.SOUTH;
                    }
                    coords = tmp.relative(facing);
                }
                case SOUTH_WEST -> {
                    if (facing == BlockFace.NORTH) {
                        facing = BlockFace.WEST;
                    } else { // EAST
                        facing = BlockFace.SOUTH;
                    }
                    coords = tmp.relative(facing);
                }
                case ASCENDING_EAST, ASCENDING_SOUTH, ASCENDING_WEST, ASCENDING_NORTH -> {
                    coords = tmp.relative(facing).relative(BlockFace.UP);
                    if (PipeShape.get(start.getWorld(), coords) == null) {
                        // try on same level
                        coords = tmp.relative(facing);
                    }
                }
                default -> { // EAST_WEST, NORTH_SOUTH
                    // no change continue straight
                    coords = tmp.relative(facing);
                }
            }
            if (PipeShape.get(start.getWorld(), coords) == null) {
                // try below
                coords = tmp.relative(facing).relative(BlockFace.DOWN);
            }
            shape = PipeShape.get(start.getWorld(), coords);
        }
        coords = coords.relative(BlockFace.UP);
        return new PipeData(new Location(start.getWorld(), coords.getX(), coords.getY(), coords.getZ()), length);
    }

    public class PipeData {

        private final Location exit;
        private final int length;

        public PipeData(Location exit, int length) {
            this.exit = exit;
            this.length = length;
        }

        public Location getExit() {
            return exit;
        }

        public int getLength() {
            return length;
        }
    }
}
