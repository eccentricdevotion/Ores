package me.eccentric_nz.ores.common;

import me.eccentric_nz.ores.mOre;
import me.eccentric_nz.ores.nuclear.NuclearInventory;
import me.eccentric_nz.ores.nuclear.NuclearStorage;
import me.eccentric_nz.ores.ore.OreBroadcast;
import me.eccentric_nz.ores.ore.OreCounter;
import me.eccentric_nz.ores.ore.OreData;
import me.eccentric_nz.ores.ore.OreType;
import me.eccentric_nz.ores.pipe.CustomBlockData;
import me.eccentric_nz.ores.pipe.PipeCoords;
import me.eccentric_nz.ores.pipe.PipeFrame;
import me.eccentric_nz.ores.pipe.PipeShape;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CommonListener implements Listener {

    private final mOre plugin;
    private final OreCounter counter;
    private final OreBroadcast oreBroadcast;
    private final HashMap<UUID, Block> nuclearViewers = new HashMap<>();
    private final List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    public CommonListener(mOre plugin) {
        this.plugin = plugin;
        counter = new OreCounter();
        oreBroadcast = new OreBroadcast();
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
            return;
        }
        Block block = event.getBlockPlaced();
        if (isCollector(is)) {
            // get direction player is facing
            // then set BlockData of brown mushroom block
            switch (player.getFacing()) {
                case EAST -> {
                    block.setBlockData(OreData.collectorEastMushroom);
                }
                case SOUTH -> {
                    block.setBlockData(OreData.collectorSouthMushroom);
                }
                case WEST -> {
                    block.setBlockData(OreData.collectorWestMushroom);
                }
                case NORTH -> {
                    block.setBlockData(OreData.collectorNorthMushroom);
                }
                default -> {
                    event.setCancelled(true);
                }
            }
            return;
        }
        if (isOre(is)) {
            ItemMeta im = is.getItemMeta();
            int cmd = im.getCustomModelData();
            OreType ore = OreType.values()[cmd - 1000];
            block.setBlockData(ore.getData());
        }
        if (isGenerator(is)) {
            block.setBlockData(OreData.nuclearGeneratorMushroom);
            // set persistent data
            CustomBlockData customBlockData = new CustomBlockData(block, plugin);
            customBlockData.set(mOre.getGeneratorKey(), PersistentDataType.INTEGER, 0);
            customBlockData.set(mOre.getWetKey(), PersistentDataType.INTEGER, 0);
            // save block storage
            NuclearStorage.addBlock(block);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!block.getType().equals(Material.BROWN_MUSHROOM_BLOCK)) {
            return;
        }
        MultipleFacing data = (MultipleFacing) block.getBlockData();
        // must use pickaxe
        Player player = event.getPlayer();
        ItemStack pick = player.getInventory().getItemInMainHand();
        boolean pickaxe = pick != null && isPickAxe(pick.getType());
        ItemStack is = null;
        if (data.matches(OreData.nuclearGeneratorMushroom)) {
            event.setCancelled(true);
            is = new ItemStack(Material.BROWN_MUSHROOM_BLOCK);
            ItemMeta im = is.getItemMeta();
            im.setCustomModelData(1004);
            im.getPersistentDataContainer().set(mOre.getPipeKey(), PersistentDataType.INTEGER, 1);
            im.setDisplayName("Nuclear Generator");
            is.setItemMeta(im);
            // remove block storage
            NuclearStorage.removeBlock(block);
        }
        if (OreData.isCollectorMushroom(data)) {
            event.setCancelled(true);
            is = new ItemStack(Material.BROWN_MUSHROOM_BLOCK);
            ItemMeta im = is.getItemMeta();
            im.setCustomModelData(1003);
            im.getPersistentDataContainer().set(mOre.getCollectorKey(), PersistentDataType.INTEGER, 1);
            im.setDisplayName("Lead Collector");
            is.setItemMeta(im);
        }
        if (OreData.isOreMushroom(data)) {
            Location loc = event.getBlock().getLocation();
            boolean announce = player.hasPermission("ores.announce");
            if (!counter.isAnnounceable(loc)) {
                counter.removeAnnouncedOrPlacedBlock(loc);
                announce = false;
            }
            if (announce) {
                int count = counter.getTotalBlocks(event.getBlock());
                oreBroadcast.handleBroadcast(data, count, player);
            }
            event.setCancelled(true);
            OreType ore;
            if (data.matches(OreData.bauxiteMushroom)) {
                ore = OreType.ALUMINIUM;
            } else if (data.matches(OreData.uraniumMushroom)) {
                ore = OreType.URANIUM;
            } else { // leadMushroom
                ore = OreType.LEAD;
            }
            // check for silk touch pickaxe
            ItemMeta im;
            if (hasSilkTouch(pick)) {
                is = new ItemStack(Material.BROWN_MUSHROOM_BLOCK);
                im = is.getItemMeta();
                im.setCustomModelData(1000 + ore.ordinal());
            } else {
                is = new ItemStack(ore.getMaterial());
                is.setAmount(ore.getDropCount());
                im = is.getItemMeta();
                im.setCustomModelData(1000);
            }
            im.getPersistentDataContainer().set(mOre.getOreKey(), PersistentDataType.INTEGER, ore.ordinal());
            im.setDisplayName(ore.getOreName());
            is.setItemMeta(im);
        }
        if (is != null) {
            // set block to air
            block.setType(Material.AIR);
            if (pickaxe) {
                // drop custom brown mushroom block / raw_ore
                block.getWorld().dropItem(block.getLocation(), is);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHitEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame frame && event.getDamager() instanceof Player) {
            ItemStack is = frame.getItem();
            if (isPipe(is)) {
                event.setCancelled(true);
                // drop lead pipe
                ItemStack pipe = new ItemStack(Material.STRING);
                ItemMeta leadItemMeta = pipe.getItemMeta();
                leadItemMeta.setDisplayName("Lead Pipe");
                leadItemMeta.setCustomModelData(1001);
                leadItemMeta.getPersistentDataContainer().set(mOre.getPipeKey(), PersistentDataType.INTEGER, 1);
                pipe.setItemMeta(leadItemMeta);
                frame.getWorld().dropItem(frame.getLocation(), pipe);
                // remove item frame
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, frame::remove, 1L);
            }
        }
    }

//    @EventHandler(priority = EventPriority.NORMAL)
//    public void onInteractEntity(PlayerInteractEntityEvent event) {
//        if (event.getRightClicked() instanceof ItemFrame frame) {
//            ItemStack is = frame.getItem();
//            if (isPipe(is)) {
//                PipeShape shape = PipeShape.get(frame.getWorld(), new PipeCoords(frame.getLocation().getBlockX(), frame.getLocation().getBlockY(), frame.getLocation().getBlockZ()));
//                if (shape != null) {
//                    Location end = new PipePath().getExit(frame.getLocation(), shape, event.getPlayer().getFacing()).getExit();
//                    end.getBlock().setType(Material.LIGHT_BLUE_CARPET);
//                }
//                event.setCancelled(true);
//            }
//        }
//    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        Block block = event.getClickedBlock();
        if (!block.getType().equals(Material.BROWN_MUSHROOM_BLOCK)) {
            return;
        }
        if (block.getBlockData().matches(OreData.nuclearGeneratorMushroom)) {
            Player player = event.getPlayer();
            Inventory inventory = Bukkit.getServer().createInventory(player, 9, "Nuclear Generator");
            inventory.setContents(NuclearInventory.getInventory(block));
            player.openInventory(inventory);
            // track player and block
            nuclearViewers.put(player.getUniqueId(), block);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals("Nuclear Generator")) {
            return;
        }
        UUID uuid = event.getPlayer().getUniqueId();
        if (nuclearViewers.containsKey(uuid)) {
            Inventory inventory = event.getInventory();
            // count the number of Uranium Pellets and
            // remove any items that aren't Uranium Pellets
            int amount = 0;
            List<ItemStack> illegal = new ArrayList<>();
            for (ItemStack is : inventory.getContents()) {
                if (is != null) {
                    if (is.isSimilar(NuclearInventory.pellet)) {
                        amount += is.getAmount();
                    } else {
                        illegal.add(is);
                    }
                }
            }
            Block block = nuclearViewers.get(uuid);
            CustomBlockData customBlockData = new CustomBlockData(block, plugin);
            customBlockData.set(mOre.getGeneratorKey(), PersistentDataType.INTEGER, amount);
            if (illegal.size() > 0) {
                for (ItemStack is : illegal) {
                    block.getWorld().dropItem(block.getLocation().add(0.5, 1, 0.5), is);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.BROWN_MUSHROOM_BLOCK)) {
            event.setCancelled(true);
            event.getBlock().getState().update(true, false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(final BlockFromToEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.WATER)) {
            // check if is at the end of a pipe
            if (hasPipe(block)) {
                event.setCancelled(true);
            }
            return;
        }
    }

    private boolean hasPipe(Block block) {
        boolean hasPipe = false;
        for (BlockFace face : faces) {
            Block b = block.getRelative(face);
            if (PipeShape.isPipe(block.getWorld(), new PipeCoords(block.getX(), block.getY(), block.getZ()))) {
                hasPipe = true;
                break;
            }
        }
        return hasPipe;
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
        if (!is.getItemMeta().getPersistentDataContainer().has(mOre.getPipeKey(), PersistentDataType.INTEGER)) {
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
        if (!is.getItemMeta().getPersistentDataContainer().has(mOre.getCollectorKey(), PersistentDataType.INTEGER)) {
            return false;
        }
        return is.getItemMeta().hasCustomModelData();
    }

    private boolean isGenerator(ItemStack is) {
        if (is == null) {
            return false;
        }
        if (!is.getType().equals(Material.BROWN_MUSHROOM_BLOCK)) {
            return false;
        }
        if (!is.hasItemMeta()) {
            return false;
        }
        if (!is.getItemMeta().getPersistentDataContainer().has(mOre.getGeneratorKey(), PersistentDataType.INTEGER)) {
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
        if (!is.getItemMeta().getPersistentDataContainer().has(mOre.getOreKey(), PersistentDataType.INTEGER)) {
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