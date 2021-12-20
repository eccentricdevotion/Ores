package me.eccentric_nz.ores.pipe;

import org.bukkit.Rotation;

public enum PipeShape {

    NORTH_SOUTH(Rotation.NONE, 1001),
    EAST_WEST(Rotation.CLOCKWISE, 1001),
    ASCENDING_EAST(Rotation.CLOCKWISE, 1003),
    ASCENDING_WEST(Rotation.COUNTER_CLOCKWISE, 1003),
    ASCENDING_NORTH(Rotation.NONE, 1003),
    ASCENDING_SOUTH(Rotation.FLIPPED, 1003),
    SOUTH_EAST(Rotation.NONE, 1002),
    SOUTH_WEST(Rotation.CLOCKWISE, 1002),
    NORTH_WEST(Rotation.FLIPPED, 1002),
    NORTH_EAST(Rotation.COUNTER_CLOCKWISE, 1002);

    private final Rotation rotation;
    private final int customModelData;

    PipeShape(Rotation rotation, int customModelData) {
        this.rotation = rotation;
        this.customModelData = customModelData;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public boolean isStraight() {
        return this == PipeShape.NORTH_SOUTH || this == PipeShape.EAST_WEST;
    }

    public boolean isCurved() {
        return this == PipeShape.NORTH_EAST || this == PipeShape.NORTH_WEST || this == PipeShape.SOUTH_EAST || this == PipeShape.SOUTH_WEST;
    }

    public boolean isAscending() {
        return this == PipeShape.ASCENDING_NORTH || this == PipeShape.ASCENDING_EAST || this == PipeShape.ASCENDING_SOUTH || this == PipeShape.ASCENDING_WEST;
    }
}
