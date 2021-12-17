package me.eccentric_nz.ores;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Ores extends JavaPlugin {

    private static NamespacedKey oreKey;
    private static NamespacedKey pipeKey;
    private static NamespacedKey collectorKey;
    private static NamespacedKey connectionKey;
    String pluginName;

    public static NamespacedKey getOreKey() {
        return oreKey;
    }

    public static NamespacedKey getPipeKey() {
        return pipeKey;
    }

    public static NamespacedKey getCollectorKey() {
        return collectorKey;
    }

    public static NamespacedKey getConnectionKey() {
        return connectionKey;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        oreKey = new NamespacedKey(this, "custom_ore");
        pipeKey = new NamespacedKey(this, "lead_pipe");
        collectorKey = new NamespacedKey(this, "lead_collector");
        connectionKey = new NamespacedKey(this, "connection");
        saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdfFile = getDescription();
        pluginName = ChatColor.GOLD + "[" + pdfFile.getName() + "]" + ChatColor.RESET + " ";
        pm.registerEvents(new OresListener(this), this);
        pm.registerEvents(new PipesListener(this), this);
        pm.registerEvents(new OreSmelter(this), this);
        OresCommand command = new OresCommand(this);
        getCommand("ore").setExecutor(command);
        getCommand("pipe").setExecutor(command);
        OresTabCompleter completer = new OresTabCompleter();
        getCommand("ore").setTabCompleter(completer);
        getCommand("pipe").setTabCompleter(completer);
        new OresRecipes(this).addRecipes();
    }
}