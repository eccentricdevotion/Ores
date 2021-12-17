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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PipesListener implements Listener {

    private final Ores plugin;
    private final NoteBlock collectorE;
    private final NoteBlock collectorS;
    private final NoteBlock collectorW;
    private final NoteBlock collectorN;

    PipesListener(Ores plugin) {
        this.plugin = plugin;
        collectorE = (NoteBlock) this.plugin.getServer().createBlockData(Material.NOTE_BLOCK);
        collectorE.setInstrument(Instrument.BIT);
        collectorE.setNote(new Note(21));
        collectorS = (NoteBlock) this.plugin.getServer().createBlockData(Material.NOTE_BLOCK);
        collectorS.setInstrument(Instrument.BIT);
        collectorS.setNote(new Note(22));
        collectorW = (NoteBlock) this.plugin.getServer().createBlockData(Material.NOTE_BLOCK);
        collectorW.setInstrument(Instrument.BIT);
        collectorW.setNote(new Note(23));
        collectorN = (NoteBlock) this.plugin.getServer().createBlockData(Material.NOTE_BLOCK);
        collectorN.setInstrument(Instrument.BIT);
        collectorN.setNote(new Note(24));
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
            // get direction player is facing
            // then set BlockData of note block
            switch (player.getFacing()) {
                case EAST -> {
                    event.getBlockPlaced().setBlockData(collectorW);
                }
                case SOUTH -> {
                    event.getBlockPlaced().setBlockData(collectorN);
                }
                case WEST -> {
                    event.getBlockPlaced().setBlockData(collectorE);
                }
                case NORTH -> {
                    event.getBlockPlaced().setBlockData(collectorS);
                }
                default -> {
                    event.setCancelled(true);
                }
            }
            if (!event.isCancelled()) {
                // set persistent data
                CustomBlockData customBlockData = new CustomBlockData(event.getBlockPlaced(), plugin);
                customBlockData.set(Ores.getPipeKey(), PersistentDataType.INTEGER, 1);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        CustomBlockData customBlockData = new CustomBlockData(block, plugin);
        if (!customBlockData.isEmpty() && customBlockData.has(Ores.getPipeKey(), PersistentDataType.INTEGER)) {
            // set block to air
            block.setType(Material.AIR);
            // drop custom note block
            ItemStack is = new ItemStack(Material.NOTE_BLOCK);
            ItemMeta im = is.getItemMeta();
            im.setCustomModelData(1000);
            im.getPersistentDataContainer().set(Ores.getPipeKey(), PersistentDataType.INTEGER, 1);
            im.setDisplayName("Lead Collector");
            is.setItemMeta(im);
            block.getWorld().dropItemNaturally(block.getLocation(), is);
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
        CustomBlockData customBlockData = new CustomBlockData(block, plugin);
        if (customBlockData.isEmpty()) {
            return;
        }
        if (!customBlockData.has(Ores.getPipeKey(), PersistentDataType.INTEGER)) {
            return;
        }
        event.setCancelled(true);
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            block.setType(Material.AIR);
        }
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