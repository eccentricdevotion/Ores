package me.eccentric_nz.ores.pipe;

import me.eccentric_nz.ores.mOre;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Locale;

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

    public static boolean isPipe(World world, PipeCoords coords) {
        Collection<Entity> entities = world.getNearbyEntities(PipeCoords.getLocation(world, coords), 0.4d, 0.4d, 0.4d, (e) -> e.getType() == EntityType.ITEM_FRAME);
        if (entities.size() > 0) {
            return PipeFrame.isPipe((ItemFrame) entities.iterator().next());
        }
        return false;
    }

    public static PipeShape get(World world, PipeCoords coords) {
        Collection<Entity> entities = world.getNearbyEntities(PipeCoords.getLocation(world, coords), 0.25d, 0.25d, 0.25d, (e) -> e.getType() == EntityType.ITEM_FRAME);
        if (entities.size() > 0) {
            ItemFrame frame = (ItemFrame) entities.iterator().next();
            if (frame.getPersistentDataContainer().has(mOre.getPipeKey(), PersistentDataType.STRING)) {
                String shape = frame.getPersistentDataContainer().get(mOre.getPipeKey(), PersistentDataType.STRING);
                if (shape != null) {
                    return PipeShape.valueOf(shape.toUpperCase(Locale.ROOT));
                }
            }
        }
        return null;
    }

    public static void updateFrame(World world, PipeCoords coords, PipeShape shape) {
        Collection<Entity> entities = world.getNearbyEntities(PipeCoords.getLocation(world, coords), 0.25d, 0.25d, 0.25d, (e) -> e.getType() == EntityType.ITEM_FRAME);
        if (entities.size() > 0) {
            ItemFrame frame = (ItemFrame) entities.iterator().next();
            frame.getPersistentDataContainer().set(mOre.getPipeKey(), PersistentDataType.STRING, shape.toString());
            ItemStack is = frame.getItem();
            ItemMeta im = is.getItemMeta();
            im.setCustomModelData(shape.getCustomModelData());
            is.setItemMeta(im);
            frame.setItem(is);
            frame.setRotation(shape.getRotation());
        }
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
