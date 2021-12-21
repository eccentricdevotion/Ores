package me.eccentric_nz.ores.pipe;

import com.google.common.collect.Lists;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.List;

public class PipeLogic {
    private final World world;
    private final PipeCoords coords;
    private final boolean isStraight;
    private final List<PipeCoords> connections = Lists.newArrayList();
    private PipeShape shape;

    public PipeLogic(World world, PipeCoords coords, PipeShape shape) {
        this.world = world;
        this.coords = coords;
        this.shape = shape;
        isStraight = shape.isStraight();
        updateConnections(shape);
    }

    private void updateConnections(PipeShape shape) {
        connections.clear();
        switch (shape) {
            case NORTH_SOUTH -> {
                connections.add(coords.north());
                connections.add(coords.south());
            }
            case EAST_WEST -> {
                connections.add(coords.west());
                connections.add(coords.east());
            }
            case ASCENDING_EAST -> {
                connections.add(coords.west());
                connections.add(coords.east().above());
            }
            case ASCENDING_WEST -> {
                connections.add(coords.west().above());
                connections.add(coords.east());
            }
            case ASCENDING_NORTH -> {
                connections.add(coords.north().above());
                connections.add(coords.south());
            }
            case ASCENDING_SOUTH -> {
                connections.add(coords.north());
                connections.add(coords.south().above());
            }
            case SOUTH_EAST -> {
                connections.add(coords.east());
                connections.add(coords.south());
            }
            case SOUTH_WEST -> {
                connections.add(coords.west());
                connections.add(coords.south());
            }
            case NORTH_WEST -> {
                connections.add(coords.west());
                connections.add(coords.north());
            }
            case NORTH_EAST -> {
                connections.add(coords.east());
                connections.add(coords.north());
            }
        }
    }

    private void removeSoftConnections() {
        for (int i = 0; i < connections.size(); ++i) {
            PipeLogic logic = getPipe(connections.get(i));
            if (logic != null && logic.connectsTo(this)) {
                connections.set(i, logic.coords);
            } else {
                connections.remove(i--);
            }
        }
    }

    @Nullable
    private PipeLogic getPipe(PipeCoords coords) {
        if (PipeShape.isPipe(world, coords)) {
            return new PipeLogic(world, coords, PipeShape.get(world, coords));
        } else {
            PipeCoords position = coords.above();
            if (PipeShape.isPipe(world, position)) {
                return new PipeLogic(world, position, PipeShape.get(world, position));
            } else {
                position = coords.below();
                return PipeShape.isPipe(world, position) ? new PipeLogic(world, position, PipeShape.get(world, position)) : null;
            }
        }
    }

    private boolean connectsTo(PipeLogic logic) {
        return hasConnection(logic.coords);
    }

    private boolean hasConnection(PipeCoords coords) {
        for (PipeCoords connection : connections) {
            if (connection.getX() == coords.getX() && connection.getZ() == coords.getZ()) {
                return true;
            }
        }
        return false;
    }

    private boolean canConnectTo(PipeLogic logic) {
        return connectsTo(logic) || connections.size() != 2;
    }

    private void connectTo(PipeLogic logic) {
        connections.add(logic.coords);
        PipeCoords north = coords.north();
        PipeCoords south = coords.south();
        PipeCoords west = coords.west();
        PipeCoords east = coords.east();
        boolean hasConnectionNorth = hasConnection(north);
        boolean hasConnectionSouth = hasConnection(south);
        boolean hasConnectionWest = hasConnection(west);
        boolean hasConnectionEast = hasConnection(east);
        PipeShape newShape = null;
        if (hasConnectionNorth || hasConnectionSouth) {
            newShape = PipeShape.NORTH_SOUTH;
        }
        if (hasConnectionWest || hasConnectionEast) {
            newShape = PipeShape.EAST_WEST;
        }
        if (hasConnectionSouth && hasConnectionEast && !hasConnectionNorth && !hasConnectionWest) {
            newShape = PipeShape.SOUTH_EAST;
        }
        if (hasConnectionSouth && hasConnectionWest && !hasConnectionNorth && !hasConnectionEast) {
            newShape = PipeShape.SOUTH_WEST;
        }
        if (hasConnectionNorth && hasConnectionWest && !hasConnectionSouth && !hasConnectionEast) {
            newShape = PipeShape.NORTH_WEST;
        }
        if (hasConnectionNorth && hasConnectionEast && !hasConnectionSouth && !hasConnectionWest) {
            newShape = PipeShape.NORTH_EAST;
        }
        if (newShape == PipeShape.NORTH_SOUTH) {
            if (PipeShape.isPipe(world, north.above())) {
                newShape = PipeShape.ASCENDING_NORTH;
            }
            if (PipeShape.isPipe(world, south.above())) {
                newShape = PipeShape.ASCENDING_SOUTH;
            }
        }
        if (newShape == PipeShape.EAST_WEST) {
            if (PipeShape.isPipe(world, east.above())) {
                newShape = PipeShape.ASCENDING_EAST;
            }
            if (PipeShape.isPipe(world, west.above())) {
                newShape = PipeShape.ASCENDING_WEST;
            }
        }
        if (newShape == null) {
            newShape = PipeShape.NORTH_SOUTH;
        }
        this.shape = newShape;
        PipeShape.updateFrame(world, coords, this.shape);
    }

    private boolean hasNeighborRail(PipeCoords coords) {
        PipeLogic logic = getPipe(coords);
        if (logic == null) {
            return false;
        } else {
            logic.removeSoftConnections();
            return logic.canConnectTo(this);
        }
    }

    public PipeLogic place(PipeShape shape) {
        PipeCoords north = coords.north();
        PipeCoords south = coords.south();
        PipeCoords west = coords.west();
        PipeCoords east = coords.east();
        boolean hasConnectionNorth = hasNeighborRail(north);
        boolean hasConnectionSouth = hasNeighborRail(south);
        boolean hasConnectionWest = hasNeighborRail(west);
        boolean hasConnectionEast = hasNeighborRail(east);
        PipeShape newShape = null;
        boolean connectionNorthOrSouth = hasConnectionNorth || hasConnectionSouth;
        boolean connectionWestOrEast = hasConnectionWest || hasConnectionEast;
        if (connectionNorthOrSouth && !connectionWestOrEast) {
            newShape = PipeShape.NORTH_SOUTH;
        }
        if (connectionWestOrEast && !connectionNorthOrSouth) {
            newShape = PipeShape.EAST_WEST;
        }
        boolean southAndEast = hasConnectionSouth && hasConnectionEast;
        boolean southAndWest = hasConnectionSouth && hasConnectionWest;
        boolean northAndEast = hasConnectionNorth && hasConnectionEast;
        boolean northAndWest = hasConnectionNorth && hasConnectionWest;
        if (southAndEast && !hasConnectionNorth && !hasConnectionWest) {
            newShape = PipeShape.SOUTH_EAST;
        }
        if (southAndWest && !hasConnectionNorth && !hasConnectionEast) {
            newShape = PipeShape.SOUTH_WEST;
        }
        if (northAndWest && !hasConnectionSouth && !hasConnectionEast) {
            newShape = PipeShape.NORTH_WEST;
        }
        if (northAndEast && !hasConnectionSouth && !hasConnectionWest) {
            newShape = PipeShape.NORTH_EAST;
        }
        if (newShape == null) {
            if (connectionNorthOrSouth && connectionWestOrEast) {
                newShape = shape;
            } else if (connectionNorthOrSouth) {
                newShape = PipeShape.NORTH_SOUTH;
            } else if (connectionWestOrEast) {
                newShape = PipeShape.EAST_WEST;
            }
            if (!isStraight) {
                if (northAndWest) {
                    newShape = PipeShape.NORTH_WEST;
                }
                if (northAndEast) {
                    newShape = PipeShape.NORTH_EAST;
                }
                if (southAndWest) {
                    newShape = PipeShape.SOUTH_WEST;
                }
                if (southAndEast) {
                    newShape = PipeShape.SOUTH_EAST;
                }
            }
        }
        if (newShape == PipeShape.NORTH_SOUTH) {
            if (PipeShape.isPipe(world, north.above())) {
                newShape = PipeShape.ASCENDING_NORTH;
            }
            if (PipeShape.isPipe(world, south.above())) {
                newShape = PipeShape.ASCENDING_SOUTH;
            }
        }
        if (newShape == PipeShape.EAST_WEST) {
            if (PipeShape.isPipe(world, east.above())) {
                newShape = PipeShape.ASCENDING_EAST;
            }
            if (PipeShape.isPipe(world, west.above())) {
                newShape = PipeShape.ASCENDING_WEST;
            }
        }
        if (newShape == null) {
            newShape = shape;
        }
        updateConnections(newShape);
        this.shape = newShape;
        PipeShape.updateFrame(world, coords, this.shape);
        for (PipeCoords connection : connections) {
            PipeLogic logic = getPipe(connection);
            if (logic != null) {
                logic.removeSoftConnections();
                if (logic.canConnectTo(this)) {
                    logic.connectTo(this);
                }
            }
        }
        return this;
    }
}
