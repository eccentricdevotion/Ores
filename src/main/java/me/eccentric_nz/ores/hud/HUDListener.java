package me.eccentric_nz.ores.hud;

import me.eccentric_nz.ores.Ores;
import me.eccentric_nz.ores.OresStringUtils;
import me.eccentric_nz.ores.ore.OreData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class HUDListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Ores.getHudPlayers().containsKey(player.getUniqueId())) {
            String showVanilla = Ores.getHudPlayers().get(player.getUniqueId());
            Block block = player.getTargetBlock(null, 6);
            String material = OresStringUtils.capitalise(block.getType().toString());
            boolean show = false;
            if (material.equals("Brown Mushroom Block")) {
                MultipleFacing mushroom = (MultipleFacing) block.getBlockData();
                if (mushroom.matches(OreData.bauxiteMushroom)) {
                    material = "Bauxite Ore";
                } else if (mushroom.matches(OreData.uraniumMushroom)) {
                    material = "Uranium Ore";
                } else if (mushroom.matches(OreData.leadMushroom)) {
                    material = "Lead Ore";
                }
                show = true;
            }
            if (show || showVanilla.equals("vanilla")) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(material));
            }
        }
    }
}
