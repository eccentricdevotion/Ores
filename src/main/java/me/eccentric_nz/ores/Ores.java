package me.eccentric_nz.ores;

import me.eccentric_nz.ores.hud.HUDListener;
import me.eccentric_nz.ores.nuclear.NuclearRecipe;
import me.eccentric_nz.ores.ore.OreSmelter;
import me.eccentric_nz.ores.ore.OresWorldInit;
import me.eccentric_nz.ores.pipe.PipeRecipes;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Ores extends JavaPlugin {

    private static final HashMap<UUID, String> hudPlayers = new HashMap<>();
    private static Ores plugin;
    private static NamespacedKey oreKey;
    private static NamespacedKey pipeKey;
    private static NamespacedKey collectorKey;
    private static NamespacedKey generatorKey;
    String pluginName;

    public static Ores getPlugin() {
        return plugin;
    }

    public static NamespacedKey getOreKey() {
        return oreKey;
    }

    public static NamespacedKey getPipeKey() {
        return pipeKey;
    }

    public static NamespacedKey getCollectorKey() {
        return collectorKey;
    }

    public static NamespacedKey getGeneratorKey() {
        return generatorKey;
    }

    public static HashMap<UUID, String> getHudPlayers() {
        return hudPlayers;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        plugin = this;
        oreKey = new NamespacedKey(this, "custom_ore");
        pipeKey = new NamespacedKey(this, "lead_pipe");
        collectorKey = new NamespacedKey(this, "lead_collector");
        generatorKey = new NamespacedKey(this, "nuclear_generator");
        saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdfFile = getDescription();
        pluginName = ChatColor.GOLD + "[" + pdfFile.getName() + "]" + ChatColor.RESET + " ";
        pm.registerEvents(new OresWorldInit(this), this);
        pm.registerEvents(new OreSmelter(this), this);
        pm.registerEvents(new CommonListener(this), this);
        pm.registerEvents(new HUDListener(), this);
        OresCommand command = new OresCommand();
        getCommand("hud").setExecutor(command);
        getCommand("ore").setExecutor(command);
        getCommand("pipe").setExecutor(command);
        OresTabCompleter completer = new OresTabCompleter();
        getCommand("hud").setTabCompleter(completer);
        getCommand("ore").setTabCompleter(completer);
        getCommand("pipe").setTabCompleter(completer);
        new PipeRecipes(this).addRecipes();
        new NuclearRecipe(this).addRecipe();
    }
}