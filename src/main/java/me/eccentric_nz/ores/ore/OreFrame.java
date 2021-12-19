package me.eccentric_nz.ores.ore;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.generator.LimitedRegion;

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
//        // spawn in an item frame
//        ItemFrame frame = (ItemFrame) location.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
//        frame.setFacingDirection(BlockFace.UP);
//        // set frame's item
//        ItemStack raw = new ItemStack(ore.getMaterial());
//        ItemMeta im = raw.getItemMeta();
//        im.setDisplayName("");
//        im.setCustomModelData(999);
//        im.getPersistentDataContainer().set(Ores.getOreKey(), PersistentDataType.INTEGER, 1);
//        raw.setItemMeta(im);
//        frame.setItem(raw);
//        frame.setVisible(false);
    }
}
