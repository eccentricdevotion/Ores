package me.eccentric_nz.ores;

import me.eccentric_nz.ores.ore.OreType;
import me.eccentric_nz.ores.pipe.PipeShape;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;

public class OresCommand implements CommandExecutor {

    private final Ores plugin;

    OresCommand(Ores plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (cmd.getName().equalsIgnoreCase("ore") || cmd.getName().equalsIgnoreCase("pipe")) {
                Location location = player.getTargetBlock(null, 16).getLocation().add(0, 1, 0);
                ItemFrame frame = (ItemFrame) location.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
                frame.setFacingDirection(BlockFace.UP);
                if (cmd.getName().equalsIgnoreCase("ore")) {
                    try {
                        OreType ore = OreType.valueOf(args[0].toUpperCase(Locale.ROOT));
                        ItemStack raw = new ItemStack(ore.getMaterial());
                        ItemMeta im = raw.getItemMeta();
                        im.setDisplayName("");
                        im.setCustomModelData(999);
                        im.getPersistentDataContainer().set(Ores.getOreKey(), PersistentDataType.INTEGER, 1);
                        raw.setItemMeta(im);
                        frame.setItem(raw);
                        frame.setVisible(false);
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }
                if (cmd.getName().equalsIgnoreCase("pipe")) {
                    try {
                        PipeShape shape = PipeShape.valueOf(args[0].toUpperCase(Locale.ROOT));
                        ItemStack lead = new ItemStack(Material.STRING);
                        ItemMeta im = lead.getItemMeta();
                        im.setDisplayName("");
                        im.setCustomModelData(shape.getCustomModelData());
                        im.getPersistentDataContainer().set(Ores.getPipeKey(), PersistentDataType.INTEGER, 1);
                        lead.setItemMeta(im);
                        frame.setItem(lead);
                        frame.setRotation(shape.getRotation());
                        frame.setVisible(false);
                        frame.getPersistentDataContainer().set(Ores.getPipeKey(), PersistentDataType.STRING, shape.toString());
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
