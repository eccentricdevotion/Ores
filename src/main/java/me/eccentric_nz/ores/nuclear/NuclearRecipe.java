package me.eccentric_nz.ores.nuclear;

import me.eccentric_nz.ores.mOre;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class NuclearRecipe {

    private final mOre plugin;

    public NuclearRecipe(mOre plugin) {
        this.plugin = plugin;
    }

    public void addRecipe() {
        // nuclear power source
        ItemStack result = new ItemStack(Material.BROWN_MUSHROOM_BLOCK);
        ItemMeta resultItemMeta = result.getItemMeta();
        resultItemMeta.setDisplayName("Nuclear Generator");
        resultItemMeta.setCustomModelData(1004);
        resultItemMeta.getPersistentDataContainer().set(mOre.getGeneratorKey(), PersistentDataType.INTEGER, 0);
        result.setItemMeta(resultItemMeta);
        ShapedRecipe generator = new ShapedRecipe(mOre.getGeneratorKey(), result);
        generator.shape("ALA", "ARA", "AUA");
        // Aluminium Ingot
        ItemStack aluminium = new ItemStack(Material.IRON_INGOT);
        ItemMeta ingotItemMeta = aluminium.getItemMeta();
        ingotItemMeta.setDisplayName("Aluminium Ingot");
        ingotItemMeta.setCustomModelData(1000);
        ingotItemMeta.getPersistentDataContainer().set(mOre.getOreKey(), PersistentDataType.INTEGER, 1);
        aluminium.setItemMeta(ingotItemMeta);
        RecipeChoice exact = new RecipeChoice.ExactChoice(aluminium);
        generator.setIngredient('A', exact);
        // Uranium Pellet
        ItemStack uranium = new ItemStack(Material.COPPER_INGOT);
        ItemMeta pelletItemMeta = uranium.getItemMeta();
        pelletItemMeta.setDisplayName("Uranium Pellet");
        pelletItemMeta.setCustomModelData(1000);
        pelletItemMeta.getPersistentDataContainer().set(mOre.getOreKey(), PersistentDataType.INTEGER, 1);
        uranium.setItemMeta(pelletItemMeta);
        RecipeChoice power = new RecipeChoice.ExactChoice(uranium);
        generator.setIngredient('U', power);
        // lever
        generator.setIngredient('L', Material.LEVER);
        generator.setIngredient('R', Material.REDSTONE);
        plugin.getServer().addRecipe(generator);
    }
}
