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
    private PipeData data;

    public PipeLogic(World world, PipeCoords coords, PipeData data) {
        this.world = world;
        this.coords = coords;
        this.data = data;
        PipeShape shape = data.getShape();
        isStraight = shape.isStraight();
        updateConnections(shape);
    }

    public List<PipeCoords> getConnections() {
        return connections;
    }

    private void updateConnections(PipeShape shape) {
        connections.clear();
        switch (shape) {
            case NORTH_SOUTH:
                connections.add(coords.north());
                connections.add(coords.south());
                break;
            case EAST_WEST:
                connections.add(coords.west());
                connections.add(coords.east());
                break;
            case ASCENDING_EAST:
                connections.add(coords.west());
                connections.add(coords.east().above());
                break;
            case ASCENDING_WEST:
                connections.add(coords.west().above());
                connections.add(coords.east());
                break;
            case ASCENDING_NORTH:
                connections.add(coords.north().above());
                connections.add(coords.south());
                break;
            case ASCENDING_SOUTH:
                connections.add(coords.north());
                connections.add(coords.south().above());
                break;
            case SOUTH_EAST:
                connections.add(coords.east());
                connections.add(coords.south());
                break;
            case SOUTH_WEST:
                connections.add(coords.west());
                connections.add(coords.south());
                break;
            case NORTH_WEST:
                connections.add(coords.west());
                connections.add(coords.north());
                break;
            case NORTH_EAST:
                connections.add(coords.east());
                connections.add(coords.north());
        }
    }

    private void removeSoftConnections() {
        for (int i = 0; i < connections.size(); ++i) {
            PipeLogic logic = getPipe((PipeCoords) connections.get(i));
            if (logic != null && logic.connectsTo(this)) {
                connections.set(i, logic.coords);
            } else {
                connections.remove(i--);
            }
        }
    }

    private boolean hasPipe(PipeCoords coords) {
        return PipeData.isPipe(world, coords) || PipeData.isPipe(world, coords.above()) || PipeData.isPipe(world, coords.below());
    }

    @Nullable
    private PipeLogic getPipe(PipeCoords coords) {
        if (PipeData.isPipe(world, coords)) {
            return new PipeLogic(world, coords, PipeData.get(world, coords));
        } else {
            PipeCoords position = coords.above();
            if (PipeData.isPipe(world, position)) {
                return new PipeLogic(world, position, PipeData.get(world, position));
            } else {
                position = coords.below();
                return PipeData.isPipe(world, position) ? new PipeLogic(world, position, PipeData.get(world, position)) : null;
            }
        }
    }

    private boolean connectsTo(PipeLogic logic) {
        return hasConnection(logic.coords);
    }

    private boolean hasConnection(PipeCoords coords) {
        for (int i = 0; i < connections.size(); ++i) {
            PipeCoords connection = (PipeCoords) connections.get(i);
            if (connection.getX() == coords.getX() && connection.getZ() == coords.getZ()) {
                return true;
            }
        }
        return false;
    }

    //    protected int countPotentialConnections() {
//        int i = 0;
//        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();
//
//        while (iterator.hasNext()) {
//            EnumDirection enumdirection = (EnumDirection) iterator.next();
//
//            if (hasRail(pos.relative(enumdirection))) {
//                ++i;
//            }
//        }
//
//        return i;
//    }

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
        PipeShape shape = null;
        if (hasConnectionNorth || hasConnectionSouth) {
            shape = PipeShape.NORTH_SOUTH;
        }
        if (hasConnectionWest || hasConnectionEast) {
            shape = PipeShape.EAST_WEST;
        }
        if (!isStraight) {
            if (hasConnectionSouth && hasConnectionEast && !hasConnectionNorth && !hasConnectionWest) {
                shape = PipeShape.SOUTH_EAST;
            }
            if (hasConnectionSouth && hasConnectionWest && !hasConnectionNorth && !hasConnectionEast) {
                shape = PipeShape.SOUTH_WEST;
            }
            if (hasConnectionNorth && hasConnectionWest && !hasConnectionSouth && !hasConnectionEast) {
                shape = PipeShape.NORTH_WEST;
            }
            if (hasConnectionNorth && hasConnectionEast && !hasConnectionSouth && !hasConnectionWest) {
                shape = PipeShape.NORTH_EAST;
            }
        }
        if (shape == PipeShape.NORTH_SOUTH) {
            if (PipeData.isPipe(world, north.above())) {
                shape = PipeShape.ASCENDING_NORTH;
            }
            if (PipeData.isPipe(world, south.above())) {
                shape = PipeShape.ASCENDING_SOUTH;
            }
        }
        if (shape == PipeShape.EAST_WEST) {
            if (PipeData.isPipe(world, east.above())) {
                shape = PipeShape.ASCENDING_EAST;
            }
            if (PipeData.isPipe(world, west.above())) {
                shape = PipeShape.ASCENDING_WEST;
            }
        }
        if (shape == null) {
            shape = PipeShape.NORTH_SOUTH;
        }
        data.setShape(shape);
        PipeData.updateFrame(world, coords, data);
    }

    private boolean hasNeighborRail(PipeCoords coords) {
        PipeLogic PipeLogic = getPipe(coords);
        if (PipeLogic == null) {
            return false;
        } else {
            PipeLogic.removeSoftConnections();
            return PipeLogic.canConnectTo(this);
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
        if (!isStraight) {
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
            if (PipeData.isPipe(world, north.above())) {
                newShape = PipeShape.ASCENDING_NORTH;
            }
            if (PipeData.isPipe(world, south.above())) {
                newShape = PipeShape.ASCENDING_SOUTH;
            }
        }
        if (newShape == PipeShape.EAST_WEST) {
            if (PipeData.isPipe(world, east.above())) {
                newShape = PipeShape.ASCENDING_EAST;
            }
            if (PipeData.isPipe(world, west.above())) {
                newShape = PipeShape.ASCENDING_WEST;
            }
        }
        if (newShape == null) {
            newShape = shape;
        }
        updateConnections(newShape);
        data.setShape(newShape);
        if (PipeData.get(world, coords) != data) {
            PipeData.updateFrame(world, coords, data);
            for (int i = 0; i < connections.size(); ++i) {
                PipeLogic PipeLogic = getPipe((PipeCoords) connections.get(i));
                if (PipeLogic != null) {
                    PipeLogic.removeSoftConnections();
                    if (PipeLogic.canConnectTo(this)) {
                        PipeLogic.connectTo(this);
                    }
                }
            }
        }
        return this;
    }

    public PipeData getPipeData() {
        return data;
    }
}
