package me.eccentric_nz.ores.pipe;

import me.eccentric_nz.ores.Ores;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.UUID;

public class PipeData {

    private static PipeDataTagType pipeDataTagType;
    private UUID uuid;
    private PipeShape shape;

    public PipeData() {
        this.pipeDataTagType = new PipeDataTagType(Ores.getPlugin());
    }

    public static boolean isPipe(World world, PipeCoords coords) {
        Collection<Entity> entities = world.getNearbyEntities(PipeCoords.getLocation(world, coords), 0.25d, 0.25d, 0.25d, (e) -> e.getType() == EntityType.ITEM_FRAME);
        if (entities.size() > 0) {
            return PipeFrame.isPipe((ItemFrame) entities.iterator().next());
        }
        return false;
    }

    public static PipeData get(World world, PipeCoords coords) {
        Collection<Entity> entities = world.getNearbyEntities(PipeCoords.getLocation(world, coords), 0.25d, 0.25d, 0.25d, (e) -> e.getType() == EntityType.ITEM_FRAME);
        if (entities.size() > 0) {
            ItemFrame frame = (ItemFrame) entities.iterator().next();
            if (frame.getPersistentDataContainer().has(Ores.getPipeKey(), pipeDataTagType)) {
                return frame.getPersistentDataContainer().get(Ores.getPipeKey(), pipeDataTagType);
            }
        }
        return null;
    }

    public static void updateFrame(World world, PipeCoords coords, PipeData data) {
        Collection<Entity> entities = world.getNearbyEntities(PipeCoords.getLocation(world, coords), 0.25d, 0.25d, 0.25d, (e) -> e.getType() == EntityType.ITEM_FRAME);
        if (entities.size() > 0) {
            ItemFrame frame = (ItemFrame) entities.iterator().next();
            frame.getPersistentDataContainer().set(Ores.getPipeKey(), pipeDataTagType, data);
            ItemStack is = frame.getItem();
            ItemMeta im = is.getItemMeta();
            im.setCustomModelData(im.getCustomModelData());
            is.setItemMeta(im);
            frame.setItem(is);
            frame.setRotation(data.shape.getRotation());
        }
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
