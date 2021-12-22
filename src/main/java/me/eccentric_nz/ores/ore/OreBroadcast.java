package me.eccentric_nz.ores.ore;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Player;

public class OreBroadcast {

    private static String titleCase(String s) {
        String[] split = s.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String str : split) {
            builder.append(uppercaseFirst(str)).append(" ");
        }
        return builder.toString().trim();
    }

    private static String uppercaseFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public void handleBroadcast(BlockData data, int blockTotal, Player player) {
        broadcastFoundBlock(player, data, blockTotal);
    }

    private void broadcastFoundBlock(Player player, BlockData data, int count) {
        String matName = getFormattedName(data, count);
        if (!matName.isEmpty() && !matName.equalsIgnoreCase("s")) {
            ChatColor colour = getBlockColour(data);
            String message = String.format("You found %s%s %s", colour, ((count) == 500 ? "over 500" : String.valueOf(count)), matName);
            player.sendMessage(message);
        }
    }

    private ChatColor getBlockColour(BlockData data) {
        ChatColor colour = ChatColor.WHITE;
        MultipleFacing mushroom = (MultipleFacing) data;
        if (mushroom.matches(OreData.bauxiteMushroom)) {
            colour = ChatColor.LIGHT_PURPLE;
        } else if (mushroom.matches(OreData.uraniumMushroom)) {
            colour = ChatColor.GOLD;
        } else if (mushroom.matches(OreData.leadMushroom)) {
            colour = ChatColor.DARK_GRAY;
        }
        return colour;
    }

    private String getFormattedName(BlockData data, int count) {
        String ore;
        if (data.getMaterial().equals(Material.BROWN_MUSHROOM_BLOCK)) {
            MultipleFacing mushroom = (MultipleFacing) data;
            if (mushroom.matches(OreData.bauxiteMushroom)) {
                ore = "Bauxite Ore";
            } else if (mushroom.matches(OreData.uraniumMushroom)) {
                ore = "Uranium Ore";
            } else if (mushroom.matches(OreData.leadMushroom)) {
                ore = "Lead Ore";
            } else {
                ore = "";
            }
        } else {
            ore = titleCase(data.getMaterial().toString().replace("_", " "));
        }
        return (count > 1) ? ore + "s" : ore;
    }
}
