package me.eccentric_nz.ores.pipe;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

public class PipeCoords {

    private int x;
    private int y;
    private int z;

    public PipeCoords(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Location getLocation(World world, PipeCoords coords) {
        return new Location(world, coords.getX(), coords.getY(), coords.getZ());
    }

    public PipeCoords above() {
        return this.relative(BlockFace.UP);
    }

    public PipeCoords below() {
        return this.relative(BlockFace.DOWN);
    }

    public PipeCoords north() {
        return this.relative(BlockFace.NORTH);
    }

    public PipeCoords south() {
        return this.relative(BlockFace.SOUTH);
    }

    public PipeCoords west() {
        return this.relative(BlockFace.WEST);
    }

    public PipeCoords east() {
        return this.relative(BlockFace.EAST);
    }

    public PipeCoords relative(BlockFace face) {
        PipeDirection direction = PipeDirection.getByBlockFace(face);
        return new PipeCoords(this.getX() + direction.getStepX(), this.getY() + direction.getStepY(), this.getZ() + direction.getStepZ());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
