package me.eccentric_nz.ores.ore;

import me.eccentric_nz.ores.Ores;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class OreFrame {

    private final Ore ore;
    private final Location location;
    private final LimitedRegion region;

    public OreFrame(Ore ore, Location location, LimitedRegion region) {
        this.ore = ore;
        this.location = location;
        this.region = region;
    }

    public void spawnOre() {
        // set Location to AIR
        region.setType(location, Material.AIR);
        // spawn in an item frame
        ItemFrame frame = (ItemFrame) region.spawnEntity(location, EntityType.ITEM_FRAME);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Ores.getPlugin(), () -> {
//            Bukkit.getLogger().log(Level.INFO, "Scheduled frame set facing, item, visibility");
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
        }, 20L);
    }
}
