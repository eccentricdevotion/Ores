package me.eccentric_nz.ores;

import me.eccentric_nz.ores.ore.OreData;
import me.eccentric_nz.ores.ore.OreType;
import me.eccentric_nz.ores.pipe.CustomBlockData;
import me.eccentric_nz.ores.pipe.PipeFrame;
import me.eccentric_nz.ores.pipe.PipeShape;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Level;

public class CommonListener implements Listener {

    private final Ores plugin;

    public CommonListener(Ores plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack is = player.getInventory().getItemInMainHand();
        if (isPipe(is)) {
            event.setCancelled(true);
            // place a pipe instead
            PipeShape shape = (player.getFacing() == BlockFace.EAST || player.getFacing() == BlockFace.WEST) ? PipeShape.EAST_WEST : PipeShape.NORTH_SOUTH;
            new PipeFrame(event.getBlock().getLocation()).spawnPipe(shape);
            if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                // reduce amount in hand
                reduceInHand(player);
            }
        }
        if (isCollector(is)) {
            // get direction player is facing
            // then set BlockData of brown mushroom block
            switch (player.getFacing()) {
                case EAST -> {
                    event.getBlockPlaced().setBlockData(OreData.collectorEastMushroom);
                }
                case SOUTH -> {
                    event.getBlockPlaced().setBlockData(OreData.collectorSouthMushroom);
                }
                case WEST -> {
                    event.getBlockPlaced().setBlockData(OreData.collectorWestMushroom);
                }
                case NORTH -> {
                    event.getBlockPlaced().setBlockData(OreData.collectorNorthMushroom);
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
        if (isOre(is)) {
            ItemMeta im = is.getItemMeta();
            int cmd = im.getCustomModelData();
            OreType ore = OreType.values()[cmd];
            event.getBlockPlaced().setBlockData(ore.getData());
            // set persistent data
            CustomBlockData customBlockData = new CustomBlockData(event.getBlockPlaced(), plugin);
            customBlockData.set(Ores.getOreKey(), PersistentDataType.INTEGER, cmd);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!block.getType().equals(Material.BROWN_MUSHROOM_BLOCK)) {
            return;
        }
        CustomBlockData customBlockData = new CustomBlockData(block, plugin);
        if (!customBlockData.isEmpty()) {
            ItemStack is = null;
            if (customBlockData.has(Ores.getPipeKey(), PersistentDataType.INTEGER)) {
                is = new ItemStack(Material.BROWN_MUSHROOM_BLOCK);
                ItemMeta im = is.getItemMeta();
                im.setCustomModelData(1003);
                im.getPersistentDataContainer().set(Ores.getPipeKey(), PersistentDataType.INTEGER, 1);
                im.setDisplayName("Lead Collector");
                is.setItemMeta(im);
            }
            if (customBlockData.has(Ores.getOreKey(), PersistentDataType.INTEGER)) {
                // must use pickaxe
                ItemStack pick = event.getPlayer().getInventory().getItemInMainHand();
                boolean pickaxe = pick != null && isPickAxe(pick.getType());
                // get the custom block data integer
                int cmd = customBlockData.get(Ores.getOreKey(), PersistentDataType.INTEGER);
                OreType ore = OreType.values()[cmd];
                // check for silk touch pickaxe
                ItemMeta im;
                if (hasSilkTouch(pick)) {
                    is = new ItemStack(Material.BROWN_MUSHROOM_BLOCK);
                    im = is.getItemMeta();
                    im.setCustomModelData(1000 + cmd);
                } else {
                    is = new ItemStack(ore.getMaterial());
                    im = is.getItemMeta();
                    im.setCustomModelData(1000);
                }
                im.getPersistentDataContainer().set(Ores.getOreKey(), PersistentDataType.INTEGER, cmd);
                im.setDisplayName(ore.getOreName());
                is.setItemMeta(im);
            }
            if (is != null) {
                // set block to air
                block.setType(Material.AIR);
                // drop custom brown mushroom block
                block.getWorld().dropItemNaturally(block.getLocation(), is);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame frame) {
            ItemStack is = frame.getItem();
            if (isPipe(is)) {
                Bukkit.getLogger().log(Level.INFO, "Frame rotation = " + frame.getRotation());
                event.setCancelled(true);
            }
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
        if (!is.getType().equals(Material.BROWN_MUSHROOM_BLOCK)) {
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

    private boolean isOre(ItemStack is) {
        if (is == null) {
            return false;
        }
        if (!is.getType().equals(Material.BROWN_MUSHROOM_BLOCK)) {
            return false;
        }
        if (!is.hasItemMeta()) {
            return false;
        }
        if (!is.getItemMeta().getPersistentDataContainer().has(Ores.getOreKey(), PersistentDataType.INTEGER)) {
            return false;
        }
        return is.getItemMeta().hasCustomModelData();
    }

    private boolean isPickAxe(Material material) {
        switch (material) {
            case DIAMOND_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, STONE_PICKAXE, NETHERITE_PICKAXE -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private boolean hasSilkTouch(ItemStack is) {
        return is.containsEnchantment(Enchantment.SILK_TOUCH);
    }
}