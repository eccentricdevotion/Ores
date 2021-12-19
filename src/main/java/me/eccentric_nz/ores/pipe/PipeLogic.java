package me.eccentric_nz.ores.pipe;

import com.google.common.collect.Lists;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.List;

public class PipeLogic {
    private final World world;
    private final PipeCoords pos;
    private final boolean isStraight;
    private final List<PipeCoords> connections = Lists.newArrayList();
    private PipeData data;

    public PipeLogic(World world, PipeCoords coords, PipeData data) {
        this.world = world;
        pos = coords;
        this.data = data;
        PipeShape shape = (PipeShape) data.getShape();
        isStraight = shape.isStraight();
        updateConnections(shape);
    }

    public List<PipeCoords> getConnections() {
        return connections;
    }

    private void updateConnections(PipeShape blockpropertytrackposition) {
        connections.clear();
        switch (blockpropertytrackposition) {
            case NORTH_SOUTH:
                connections.add(pos.north());
                connections.add(pos.south());
                break;
            case EAST_WEST:
                connections.add(pos.west());
                connections.add(pos.east());
                break;
            case ASCENDING_EAST:
                connections.add(pos.west());
                connections.add(pos.east().above());
                break;
            case ASCENDING_WEST:
                connections.add(pos.west().above());
                connections.add(pos.east());
                break;
            case ASCENDING_NORTH:
                connections.add(pos.north().above());
                connections.add(pos.south());
                break;
            case ASCENDING_SOUTH:
                connections.add(pos.north());
                connections.add(pos.south().above());
                break;
            case SOUTH_EAST:
                connections.add(pos.east());
                connections.add(pos.south());
                break;
            case SOUTH_WEST:
                connections.add(pos.west());
                connections.add(pos.south());
                break;
            case NORTH_WEST:
                connections.add(pos.west());
                connections.add(pos.north());
                break;
            case NORTH_EAST:
                connections.add(pos.east());
                connections.add(pos.north());
        }
    }

    private void removeSoftConnections() {
        for (int i = 0; i < connections.size(); ++i) {
            PipeLogic PipeLogic = getPipe((PipeCoords) connections.get(i));
            if (PipeLogic != null && PipeLogic.connectsTo(this)) {
                connections.set(i, PipeLogic.pos);
            } else {
                connections.remove(i--);
            }
        }
    }

    private boolean hasPipe(PipeCoords blockposition) {
        return PipeData.isPipe(world, blockposition) || PipeData.isPipe(world, blockposition.above()) || PipeData.isPipe(world, blockposition.below());
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

    private boolean connectsTo(PipeLogic PipeLogic) {
        return hasConnection(PipeLogic.pos);
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

    private boolean canConnectTo(PipeLogic PipeLogic) {
        return connectsTo(PipeLogic) || connections.size() != 2;
    }

    private void connectTo(PipeLogic PipeLogic) {
        connections.add(PipeLogic.pos);
        PipeCoords blockposition = pos.north();
        PipeCoords blockposition1 = pos.south();
        PipeCoords blockposition2 = pos.west();
        PipeCoords blockposition3 = pos.east();
        boolean flag = hasConnection(blockposition);
        boolean flag1 = hasConnection(blockposition1);
        boolean flag2 = hasConnection(blockposition2);
        boolean flag3 = hasConnection(blockposition3);
        PipeShape shape = null;
        if (flag || flag1) {
            shape = PipeShape.NORTH_SOUTH;
        }
        if (flag2 || flag3) {
            shape = PipeShape.EAST_WEST;
        }
        if (!isStraight) {
            if (flag1 && flag3 && !flag && !flag2) {
                shape = PipeShape.SOUTH_EAST;
            }
            if (flag1 && flag2 && !flag && !flag3) {
                shape = PipeShape.SOUTH_WEST;
            }
            if (flag && flag2 && !flag1 && !flag3) {
                shape = PipeShape.NORTH_WEST;
            }
            if (flag && flag3 && !flag1 && !flag2) {
                shape = PipeShape.NORTH_EAST;
            }
        }
        if (shape == PipeShape.NORTH_SOUTH) {
            if (PipeData.isPipe(world, blockposition.above())) {
                shape = PipeShape.ASCENDING_NORTH;
            }
            if (PipeData.isPipe(world, blockposition1.above())) {
                shape = PipeShape.ASCENDING_SOUTH;
            }
        }
        if (shape == PipeShape.EAST_WEST) {
            if (PipeData.isPipe(world, blockposition3.above())) {
                shape = PipeShape.ASCENDING_EAST;
            }
            if (PipeData.isPipe(world, blockposition2.above())) {
                shape = PipeShape.ASCENDING_WEST;
            }
        }
        if (shape == null) {
            shape = PipeShape.NORTH_SOUTH;
        }
        data.setShape(shape);
        PipeData.setFrame(world, pos, data);
    }

    private boolean hasNeighborRail(PipeCoords blockposition) {
        PipeLogic PipeLogic = getPipe(blockposition);
        if (PipeLogic == null) {
            return false;
        } else {
            PipeLogic.removeSoftConnections();
            return PipeLogic.canConnectTo(this);
        }
    }

    public PipeLogic place(boolean flag, boolean flag1, PipeShape shape) {
        PipeCoords north = pos.north();
        PipeCoords south = pos.south();
        PipeCoords west = pos.west();
        PipeCoords east = pos.east();
        boolean flag2 = hasNeighborRail(north);
        boolean flag3 = hasNeighborRail(south);
        boolean flag4 = hasNeighborRail(west);
        boolean flag5 = hasNeighborRail(east);
        PipeShape newShape = null;
        boolean flag6 = flag2 || flag3;
        boolean flag7 = flag4 || flag5;
        if (flag6 && !flag7) {
            newShape = PipeShape.NORTH_SOUTH;
        }
        if (flag7 && !flag6) {
            newShape = PipeShape.EAST_WEST;
        }
        boolean flag8 = flag3 && flag5;
        boolean flag9 = flag3 && flag4;
        boolean flag10 = flag2 && flag5;
        boolean flag11 = flag2 && flag4;
        if (!isStraight) {
            if (flag8 && !flag2 && !flag4) {
                newShape = PipeShape.SOUTH_EAST;
            }
            if (flag9 && !flag2 && !flag5) {
                newShape = PipeShape.SOUTH_WEST;
            }
            if (flag11 && !flag3 && !flag5) {
                newShape = PipeShape.NORTH_WEST;
            }
            if (flag10 && !flag3 && !flag4) {
                newShape = PipeShape.NORTH_EAST;
            }
        }
        if (newShape == null) {
            if (flag6 && flag7) {
                newShape = shape;
            } else if (flag6) {
                newShape = PipeShape.NORTH_SOUTH;
            } else if (flag7) {
                newShape = PipeShape.EAST_WEST;
            }
            if (!isStraight) {
                if (flag) {
                    if (flag8) {
                        newShape = PipeShape.SOUTH_EAST;
                    }
                    if (flag9) {
                        newShape = PipeShape.SOUTH_WEST;
                    }
                    if (flag10) {
                        newShape = PipeShape.NORTH_EAST;
                    }
                    if (flag11) {
                        newShape = PipeShape.NORTH_WEST;
                    }
                } else {
                    if (flag11) {
                        newShape = PipeShape.NORTH_WEST;
                    }
                    if (flag10) {
                        newShape = PipeShape.NORTH_EAST;
                    }
                    if (flag9) {
                        newShape = PipeShape.SOUTH_WEST;
                    }
                    if (flag8) {
                        newShape = PipeShape.SOUTH_EAST;
                    }
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
        if (flag1 || PipeData.get(world, pos) != data) {
            PipeData.setFrame(world, pos, data);
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
