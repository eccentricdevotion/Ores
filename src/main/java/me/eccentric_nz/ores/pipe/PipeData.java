package me.eccentric_nz.ores.pipe;

import org.bukkit.World;

import java.util.UUID;

public class PipeData {

    private UUID uuid;
    private PipeShape shape;

    public static boolean isPipe(World world, PipeCoords coords) {
        // TODO
        return true;
    }

    public static PipeData get(World world, PipeCoords coords) {
        // TODO
        return new PipeData();
    }

    public static void setFrame(World world, PipeCoords pos, PipeData data) {
        // TODO
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public PipeShape getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = PipeShape.valueOf(shape);
    }

    public void setShape(PipeShape shape) {
        this.shape = shape;
    }
}
