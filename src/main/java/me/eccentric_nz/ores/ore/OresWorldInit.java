package me.eccentric_nz.ores.ore;

import me.eccentric_nz.ores.Ores;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;

public class OresWorldInit implements Listener {

    private final Ores plugin;

    public OresWorldInit(Ores plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();
        if (plugin.getConfig().getBoolean("worlds." + world.getName())) {
            BlockPopulator blockPopulator = new OrePopulator(plugin, world);
            world.getPopulators().add(blockPopulator);
        }
    }
}
