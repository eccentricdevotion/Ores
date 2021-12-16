package me.eccentric_nz.ores;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class OreFrame {

    private final Ore ore;
    private final Location location;

    public OreFrame(Ore ore, Location location) {
        this.ore = ore;
        this.location = location;
    }

    public void spawnOre() {
        // set Location to AIR
        location.getBlock().setType(Material.AIR);
        // spawn in an item frame
        ItemFrame frame = (ItemFrame) location.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
        frame.setFacingDirection(BlockFace.UP);
        // set frame's item
        ItemStack raw = new ItemStack(ore.getMaterial());
        ItemMeta im = raw.getItemMeta();
        im.setDisplayName("");
        im.setCustomModelData(999);
        im.getPersistentDataContainer().set(Ores.getOreKey(), PersistentDataType.INTEGER, 1);
        raw.setItemMeta(im);
        frame.setItem(raw);
        frame.setVisible(false);
    }
}
