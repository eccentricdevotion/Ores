package me.eccentric_nz.ores.ore;

import me.eccentric_nz.ores.Ores;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class OresListener implements Listener {

    private final Ores plugin;
    private final List<Material> pickaxes = Arrays.asList(Material.DIAMOND_PICKAXE, Material.GOLDEN_PICKAXE, Material.IRON_PICKAXE, Material.STONE_PICKAXE, Material.NETHERITE_PICKAXE, Material.WOODEN_PICKAXE);

    public OresListener(Ores plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame frame && event.getDamager() instanceof Player player) {
            if (!pickaxes.contains(player.getInventory().getItemInMainHand().getType())) {
                return;
            }
            ItemStack is = frame.getItem();
            if (!isOre(is)) {
                return;
            }
            event.setCancelled(true);
            int cmd = is.getItemMeta().getCustomModelData() + 1;
            ItemMeta im = is.getItemMeta();
            im.setCustomModelData(cmd);
            is.setItemMeta(im);
            frame.setItem(is);
            if (cmd > 1009) {
                // clone item stack
                ItemStack clone = frame.getItem().clone();
                clone.setAmount(3);
                ItemMeta cim = clone.getItemMeta();
                OreType ore = OreType.getByMaterial(is.getType());
                cim.setDisplayName(ore.getOreName());
                clone.setItemMeta(cim);
                // remove item frame
                frame.remove();
                // drop item
                player.getWorld().dropItemNaturally(frame.getLocation(), clone);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame frame) {
            ItemStack is = frame.getItem();
            if (!isOre(is)) {
                Bukkit.getLogger().log(Level.INFO, "Frame rotation = " + frame.getRotation());
//                return;
            }
            event.setCancelled(true);
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
        if (!is.getItemMeta().getPersistentDataContainer().has(Ores.getOreKey(), PersistentDataType.INTEGER)) {
            return false;
        }
        return is.getItemMeta().hasCustomModelData();
    }
}