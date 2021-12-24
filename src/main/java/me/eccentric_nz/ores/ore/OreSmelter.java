package me.eccentric_nz.ores.ore;

import me.eccentric_nz.ores.mOre;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class OreSmelter implements Listener {

    private final mOre plugin;

    public OreSmelter(mOre plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onOreSmelt(FurnaceSmeltEvent event) {
        ItemStack source = event.getSource();
        ItemStack result = event.getResult();
        if (isOre(source)) {
            ItemMeta im = result.getItemMeta();
            im.setCustomModelData(1000);
            im.getPersistentDataContainer().set(mOre.getOreKey(), PersistentDataType.INTEGER, 1);
            im.setDisplayName(OreType.getByMaterial(source.getType()).getIngotName());
            result.setItemMeta(im);
        }
    }

    private boolean isOre(ItemStack is) {
        if (is == null) {
            return false;
        }
        if (!is.getType().equals(Material.RAW_IRON) && !is.getType().equals(Material.RAW_GOLD) && !is.getType().equals(Material.RAW_COPPER)) {
            return false;
        }
        if (!is.hasItemMeta()) {
            return false;
        }
        if (!is.getItemMeta().getPersistentDataContainer().has(mOre.getOreKey(), PersistentDataType.INTEGER)) {
            return false;
        }
        return is.getItemMeta().hasCustomModelData();
    }
}
