package me.eccentric_nz.ores.pipe;

import me.eccentric_nz.ores.mOre;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PipeRecipes {

    private final mOre plugin;

    public PipeRecipes(mOre plugin) {
        this.plugin = plugin;
    }

    public void addRecipes() {
        // lead pipe
        ItemStack lead = new ItemStack(Material.STRING);
        ItemMeta leadItemMeta = lead.getItemMeta();
        leadItemMeta.setDisplayName("Lead Pipe");
        leadItemMeta.setCustomModelData(1001);
        leadItemMeta.getPersistentDataContainer().set(mOre.getPipeKey(), PersistentDataType.INTEGER, 1);
        lead.setItemMeta(leadItemMeta);
        ShapedRecipe pipe = new ShapedRecipe(mOre.getPipeKey(), lead);
        pipe.shape("III", "I I", "III");
        ItemStack ingot = new ItemStack(Material.GOLD_INGOT);
        ItemMeta ingotItemMeta = ingot.getItemMeta();
        ingotItemMeta.setDisplayName("Lead Ingot");
        ingotItemMeta.setCustomModelData(1000);
        ingotItemMeta.getPersistentDataContainer().set(mOre.getOreKey(), PersistentDataType.INTEGER, 1);
        ingot.setItemMeta(ingotItemMeta);
        RecipeChoice exact = new RecipeChoice.ExactChoice(ingot);
        pipe.setIngredient('I', exact);
        plugin.getServer().addRecipe(pipe);
        // lead collector
        ItemStack collector = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta collectorItemMeta = collector.getItemMeta();
        collectorItemMeta.setDisplayName("Lead Collector");
        collectorItemMeta.setCustomModelData(1000);
        collectorItemMeta.getPersistentDataContainer().set(mOre.getCollectorKey(), PersistentDataType.INTEGER, 1);
        collector.setItemMeta(collectorItemMeta);
        ShapedRecipe collectorRecipe = new ShapedRecipe(mOre.getCollectorKey(), collector);
        collectorRecipe.shape("III", "IBI", "III");
        collectorRecipe.setIngredient('I', exact);
        collectorRecipe.setIngredient('B', Material.BUCKET);
        plugin.getServer().addRecipe(collectorRecipe);
    }
}
