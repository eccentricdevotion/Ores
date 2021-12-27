package me.eccentric_nz.ores.common;

import me.eccentric_nz.ores.mOre;
import me.eccentric_nz.ores.ore.OreData;
import me.eccentric_nz.ores.ore.OreType;
import me.eccentric_nz.ores.pipe.PipeShape;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
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
import java.util.UUID;

public class mOreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (cmd.getName().equalsIgnoreCase("moregive")) {
                if (args.length < 1) {
                    player.sendMessage("Too few command arguments!");
                    return false;
                }
                mOreGiver.giveItem(args[0].toLowerCase(Locale.ROOT), player);
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("hud")) {
                // toggle HUD
                if (player.hasPermission("ores.hud")) {
                    if (mOre.getHudPlayers().containsKey(uuid)) {
                        mOre.getHudPlayers().remove(uuid);
                        player.sendMessage("HUD disabled");
                    } else {
                        String which = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "custom";
                        mOre.getHudPlayers().put(uuid, which);
                        player.sendMessage("HUD enabled");
                    }
                } else {
                    player.sendMessage("You do not have permission to use the HUD!");
                }
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("ore") || cmd.getName().equalsIgnoreCase("pipe")) {
                if (args.length < 1) {
                    player.sendMessage("Too few command arguments!");
                    return false;
                }
                if (player.hasPermission("ores.hud")) {
                    Location location = player.getTargetBlock(null, 16).getLocation().add(0, 1, 0);
                    if (cmd.getName().equalsIgnoreCase("ore")) {
                        try {
                            OreType ore = OreType.valueOf(args[0].toUpperCase(Locale.ROOT));
                            MultipleFacing mushroom;
                            switch (ore) {
                                case ALUMINIUM -> {
                                    mushroom = OreData.bauxiteMushroom;
                                }
                                case URANIUM -> {
                                    mushroom = OreData.uraniumMushroom;
                                }
                                default -> {
                                    mushroom = OreData.leadMushroom;
                                }
                            }
                            location.getBlock().setBlockData(mushroom);
                            return true;
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                    if (cmd.getName().equalsIgnoreCase("pipe")) {
                        try {
                            ItemFrame frame = (ItemFrame) location.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
                            frame.setFacingDirection(BlockFace.UP);
                            PipeShape shape = PipeShape.valueOf(args[0].toUpperCase(Locale.ROOT));
                            ItemStack lead = new ItemStack(Material.STRING);
                            ItemMeta im = lead.getItemMeta();
                            im.setDisplayName("");
                            im.setCustomModelData(shape.getCustomModelData());
                            im.getPersistentDataContainer().set(mOre.getPipeKey(), PersistentDataType.INTEGER, 1);
                            lead.setItemMeta(im);
                            frame.setItem(lead);
                            frame.setRotation(shape.getRotation());
                            frame.setVisible(false);
                            frame.getPersistentDataContainer().set(mOre.getPipeKey(), PersistentDataType.STRING, shape.toString());
                            return true;
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                } else {
                    player.sendMessage("You do not have permission to spawn in ores and pipes!");
                }
                return true;
            }
        }
        return false;
    }
}
