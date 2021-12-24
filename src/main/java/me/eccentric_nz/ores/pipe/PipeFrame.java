package me.eccentric_nz.ores.pipe;

import me.eccentric_nz.ores.mOre;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PipeFrame {

    private static Location location;

    public PipeFrame(Location l) {
        location = l;
    }

    public static boolean isPipe(ItemFrame frame) {
        ItemStack is = frame.getItem();
        if (is == null) {
            return false;
        }
        if (!is.getType().equals(Material.STRING)) {
            return false;
        }
        if (!is.hasItemMeta()) {
            return false;
        }
        if (!is.getItemMeta().getPersistentDataContainer().has(mOre.getPipeKey(), PersistentDataType.INTEGER)) {
            return false;
        }
        return is.getItemMeta().hasCustomModelData();
    }

    public void spawnPipe(PipeShape shape) {
        // set Location to AIR
        location.getBlock().setType(Material.AIR);
        // spawn in an item frame
        ItemFrame frame = (ItemFrame) location.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
        frame.setFacingDirection(BlockFace.UP);
        // set frame's item
        ItemStack pipe = new ItemStack(Material.STRING);
        ItemMeta im = pipe.getItemMeta();
        im.setDisplayName("");
        im.setCustomModelData(shape.getCustomModelData());
        im.getPersistentDataContainer().set(mOre.getPipeKey(), PersistentDataType.INTEGER, 1);
        pipe.setItemMeta(im);
        frame.setItem(pipe);
        frame.setRotation(shape.getRotation());
        frame.setVisible(false);
//        frame.setFixed(true);
        frame.getPersistentDataContainer().set(mOre.getPipeKey(), PersistentDataType.STRING, shape.toString());
        new PipeLogic(location.getWorld(), new PipeCoords(location.getBlockX(), location.getBlockY(), location.getBlockZ()), shape).place(shape);
    }
}
