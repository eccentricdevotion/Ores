package me.eccentric_nz.ores.pipe;

import org.bukkit.block.BlockFace;

public enum PipeDirection {

    DOWN(new PipeVector(0, -1, 0)),
    UP(new PipeVector(0, 1, 0)),
    NORTH(new PipeVector(0, 0, -1)),
    SOUTH(new PipeVector(0, 0, 1)),
    WEST(new PipeVector(-1, 0, 0)),
    EAST(new PipeVector(1, 0, 0));

    private PipeVector pipeVector;

    PipeDirection(PipeVector pipeVector) {
        this.pipeVector = pipeVector;
    }

    public static PipeDirection getByBlockFace(BlockFace face) {
        return PipeDirection.valueOf(face.toString());
    }

    public int getStepX() {
        return this.pipeVector.getX();
    }

    public int getStepY() {
        return this.pipeVector.getY();
    }

    public int getStepZ() {
        return this.pipeVector.getZ();
    }
}
