package me.eccentric_nz.ores;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class OresGiver {

    public static void giveItem(String which, Player player) {
        Material material = Material.BROWN_MUSHROOM_BLOCK;
        NamespacedKey key = Ores.getOreKey();
        int cmd = 1000;
        String name = OresStringUtils.capitalise(which);
        switch (which) {
            case "uranium_ore" -> {
                cmd = 1001;
            }
            case "lead_ore" -> {
                cmd = 1002;
            }
            case "raw_bauxite" -> {
                material = Material.RAW_IRON;
            }
            case "raw_uranium" -> {
                material = Material.RAW_COPPER;
            }
            case "lead_glance" -> {
                material = Material.RAW_GOLD;
            }
            case "aluminium_ingot" -> {
                material = Material.IRON_INGOT;
            }
            case "lead_ingot" -> {
                material = Material.GOLD_INGOT;
            }
            case "uranium_pellet" -> {
                material = Material.COPPER_INGOT;
            }
            case "nuclear_generator" -> {
                cmd = 1004;
                key = Ores.getGeneratorKey();
            }
            case "lead_pipe" -> {
                material = Material.STRING;
                cmd = 1001;
                key = Ores.getPipeKey();
            }
            case "lead_collector" -> {
                cmd = 1003;
                key = Ores.getCollectorKey();
            }
            default -> {
            }
        }
        ItemStack is = new ItemStack(material);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.setCustomModelData(cmd);
        im.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        is.setItemMeta(im);
        is.setAmount(64);
        player.getInventory().addItem(is);
        player.updateInventory();
    }
}
