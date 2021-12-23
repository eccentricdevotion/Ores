package me.eccentric_nz.ores.pipe;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class PipePath {

    public Location getExit(Location start, PipeShape shape, BlockFace facing) {
        PipeCoords coords = new PipeCoords(start.getBlockX(), start.getBlockY(), start.getBlockZ());
        while (shape != null) {
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
        return new Location(start.getWorld(), coords.getX(), coords.getY(), coords.getZ());
    }
}
