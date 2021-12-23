package me.eccentric_nz.ores.nuclear;

import me.eccentric_nz.ores.Ores;
import me.eccentric_nz.ores.pipe.CustomBlockData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class NuclearInventory {

    public static ItemStack pellet;

    static {
        pellet = new ItemStack(Material.COPPER_INGOT);
        ItemMeta pelletItemMeta = pellet.getItemMeta();
        pelletItemMeta.setDisplayName("Uranium Pellet");
        pelletItemMeta.setCustomModelData(1000);
        pelletItemMeta.getPersistentDataContainer().set(Ores.getOreKey(), PersistentDataType.INTEGER, 1);
        pellet.setItemMeta(pelletItemMeta);
    }

    public static void setAmount(Block block, int amount) {
        // set persistent data
        CustomBlockData customBlockData = new CustomBlockData(block, Ores.getPlugin());
        customBlockData.set(Ores.getGeneratorKey(), PersistentDataType.INTEGER, amount);
    }

    public static ItemStack[] getInventory(Block block) {
        CustomBlockData customBlockData = new CustomBlockData(block, Ores.getPlugin());
        int amount = customBlockData.get(Ores.getGeneratorKey(), PersistentDataType.INTEGER);
        ItemStack[] contents = new ItemStack[9];
        if (amount > 0) {
            // max stack size is 64
            int leftover = amount % 64;
            int last = amount / 64;
            for (int i = 0; i <= last; i++) {
                ItemStack pellet = NuclearInventory.pellet.clone();
                int stackCount = (i == last) ? leftover : 64;
                pellet.setAmount(stackCount);
                contents[i] = pellet;
            }
        }
        return contents;
    }
}
