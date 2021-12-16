package me.eccentric_nz.ores;

import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PipesListener implements Listener {

    private final Ores plugin;
    private NoteBlock collector;
    private Note note = new Note(24);

    PipesListener(Ores plugin) {
        this.plugin = plugin;
        collector = (NoteBlock) this.plugin.getServer().createBlockData(Material.NOTE_BLOCK);
        collector.setInstrument(Instrument.BIT);
        collector.setNote(note);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack is = player.getInventory().getItemInMainHand();
        if (isPipe(is)) {
            event.setCancelled(true);
            // place a pipe instead
            new LeadPipeFrame(event.getBlock().getLocation(), player).spawnPipe();
            if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                // reduce amount in hand
                reduceInHand(player);
            }
        }
        if (isCollector(is)) {
            // set BlockData of note block
            event.getBlockPlaced().setBlockData(collector);
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!block.getType().equals(Material.NOTE_BLOCK)) {
            return;
        }
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof NoteBlock noteBlock)) {
            return;
        }
        if (!noteBlock.matches(collector)) {
            return;
        }
        event.setCancelled(true);
    }

    private void reduceInHand(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        int count = itemStack.getAmount();
        if (count - 1 > 0) {
            itemStack.setAmount(count - 1);
        } else {
            itemStack = null;
        }
        player.getInventory().setItemInMainHand(itemStack);
    }

    private boolean isPipe(ItemStack is) {
        if (is == null) {
            return false;
        }
        if (!is.getType().equals(Material.STRING)) {
            return false;
        }
        if (!is.hasItemMeta()) {
            return false;
        }
        if (!is.getItemMeta().getPersistentDataContainer().has(Ores.getPipeKey(), PersistentDataType.INTEGER)) {
            return false;
        }
        return is.getItemMeta().hasCustomModelData();
    }

    private boolean isCollector(ItemStack is) {
        if (is == null) {
            return false;
        }
        if (!is.getType().equals(Material.NOTE_BLOCK)) {
            return false;
        }
        if (!is.hasItemMeta()) {
            return false;
        }
        if (!is.getItemMeta().getPersistentDataContainer().has(Ores.getPipeKey(), PersistentDataType.INTEGER)) {
            return false;
        }
        return is.getItemMeta().hasCustomModelData();
    }
}