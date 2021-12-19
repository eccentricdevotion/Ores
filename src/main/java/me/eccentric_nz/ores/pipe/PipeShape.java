package me.eccentric_nz.ores.pipe;

public enum PipeShape {

    NORTH_SOUTH("north_south"),
    EAST_WEST("east_west"),
    ASCENDING_EAST("ascending_east"),
    ASCENDING_WEST("ascending_west"),
    ASCENDING_NORTH("ascending_north"),
    ASCENDING_SOUTH("ascending_south"),
    SOUTH_EAST("south_east"),
    SOUTH_WEST("south_west"),
    NORTH_WEST("north_west"),
    NORTH_EAST("north_east");

    private final String name;

    PipeShape(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public boolean isAscending() {
        return this == PipeShape.ASCENDING_NORTH || this == PipeShape.ASCENDING_EAST || this == PipeShape.ASCENDING_SOUTH || this == PipeShape.ASCENDING_WEST;
    }

    public boolean isStraight() {
        return this == PipeShape.NORTH_SOUTH || this == PipeShape.EAST_WEST;
    }

    public String getSerializedName() {
        return this.name;
    }
}
