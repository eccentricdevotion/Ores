package me.eccentric_nz.ores.nuclear;

import me.eccentric_nz.ores.pipe.PipeShape;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;

public class NuclearData {

    private PipeShape shape;
    private Location start;
    private Location end;
    private BlockFace facing;
    private Hopper hopper;
    private Block water;
    private Block generator;
    private boolean powered;
    private boolean source;
    private boolean receiver;
    private boolean wet;
    private int length;

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

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        this.end = end;
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

    public boolean hasReceiver() {
        return receiver;
    }

    public void setReceiver(boolean receiver) {
        this.receiver = receiver;
    }

    public boolean isWet() {
        return wet;
    }

    public void setWet(boolean wet) {
        this.wet = wet;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
