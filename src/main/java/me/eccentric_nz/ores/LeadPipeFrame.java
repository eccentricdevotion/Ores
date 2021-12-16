package me.eccentric_nz.ores;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class LeadPipeFrame {

    private static final List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST);
    private static Location location;
    private static ItemStack left;
    private static ItemStack right;
    private final Player player;

    public LeadPipeFrame(Location l, Player player) {
        location = l;
        this.player = player;
        left = new ItemStack(Material.STRING);
        ItemMeta lim = left.getItemMeta();
        lim.setDisplayName("");
        lim.setCustomModelData(Pipe.LEFT.getCustomeModelData());
        lim.getPersistentDataContainer().set(Ores.getPipeKey(), PersistentDataType.INTEGER, 1);
        left.setItemMeta(lim);
        right = new ItemStack(Material.STRING);
        ItemMeta rim = right.getItemMeta();
        rim.setDisplayName("");
        rim.setCustomModelData(Pipe.RIGHT.getCustomeModelData());
        rim.getPersistentDataContainer().set(Ores.getPipeKey(), PersistentDataType.INTEGER, 1);
        right.setItemMeta(rim);
    }

    public static BlockFace getFacing(Player player) {
        float yaw = player.getLocation().getYaw();
        Bukkit.getLogger().log(Level.INFO, "Yaw: " + yaw);
        BlockFace facing = BlockFace.SELF;
        // south
        if (yaw > -45 && yaw <= 45) {
            facing = BlockFace.SOUTH;
        }
        // west
        if (yaw > 45 && yaw <= 135) {
            facing = BlockFace.WEST;
        }
        // north
        if (yaw > 135 && yaw <= 180) {
            facing = BlockFace.NORTH;
        }
        // east
        if (yaw > -180 && yaw <= -45) {
            facing = BlockFace.EAST;
        }
        Bukkit.getLogger().log(Level.INFO, "facing: " + facing);
        return facing;
    }

    public static Rotation getRotation(BlockFace face) {
        // check for item frames in front of the location i n direction the player is facing
        Location potential = location.getBlock().getRelative(face).getLocation();
        List<Entity> ents = List.copyOf(location.getWorld().getNearbyEntities(potential, 0.4d, 0.4d, 0.4d, (e) -> e.getType() == EntityType.ITEM_FRAME));
        if (ents.size() > 0) {
            ItemFrame frame = (ItemFrame) ents.get(0);
            if (isPipe(frame)) {
                switch (frame.getRotation()) {
                    case CLOCKWISE -> {
                        // facing west
                        switch (face) {
                            case SOUTH -> {
                                frame.setItem(left);
                                frame.setRotation(Rotation.FLIPPED);
                                return Rotation.NONE;
                            }
                            case NORTH -> {
                                frame.setItem(right);
                                frame.setRotation(Rotation.NONE);
                                return Rotation.FLIPPED;
                            }
                            default -> {
                                return Rotation.CLOCKWISE;
                            }
                        }
                    }
                    case COUNTER_CLOCKWISE -> {
                        // facing east
                        switch (face) {
                            case SOUTH -> {
                                frame.setItem(right);
                                frame.setRotation(Rotation.FLIPPED);
                                return Rotation.FLIPPED;
                            }
                            case NORTH -> {
                                frame.setItem(left);
                                frame.setRotation(Rotation.NONE);
                                return Rotation.NONE;
                            }
                            default -> {
                                return Rotation.COUNTER_CLOCKWISE;
                            }
                        }
                    }
                    case FLIPPED -> {
                        // facing north
                        switch (face) {
                            case WEST -> {
                                frame.setItem(left);
                                frame.setRotation(Rotation.COUNTER_CLOCKWISE);
                                return Rotation.CLOCKWISE;
                            }
                            case EAST -> {
                                frame.setItem(right);
                                frame.setRotation(Rotation.CLOCKWISE);
                                return Rotation.COUNTER_CLOCKWISE;

                            }
                            default -> {
                                return Rotation.FLIPPED;
                            }
                        }
                    }
                    case NONE -> {
                        // facing south
                        switch (face) {
                            case WEST -> {
                                frame.setItem(right);
                                frame.setRotation(Rotation.COUNTER_CLOCKWISE);
                                return Rotation.COUNTER_CLOCKWISE;
                            }
                            case EAST -> {
                                frame.setItem(left);
                                frame.setRotation(Rotation.CLOCKWISE);
                                return Rotation.CLOCKWISE;

                            }
                            default -> {
                                return Rotation.NONE;
                            }
                        }
                    }
                    default -> {
                        switch (face) {
                            case EAST -> {
                                return Rotation.COUNTER_CLOCKWISE;
                            }
                            case SOUTH -> {
                                return Rotation.NONE;
                            }
                            case WEST -> {
                                return Rotation.CLOCKWISE;
                            }
                            default -> { // NORTH
                                return Rotation.FLIPPED;
                            }
                        }
                    }
                }
            }
        }
        switch (face) {
            case EAST -> {
                return Rotation.COUNTER_CLOCKWISE;
            }
            case SOUTH -> {
                return Rotation.NONE;
            }
            case WEST -> {
                return Rotation.CLOCKWISE;
            }
            default -> { // NORTH
                return Rotation.FLIPPED;
            }
        }
    }

    private static boolean isPipe(ItemFrame frame) {
        ItemStack is = frame.getItem();
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

    public void spawnPipe() {
        // set Location to AIR
        location.getBlock().setType(Material.AIR);
        // spawn in an item frame
        ItemFrame frame = (ItemFrame) location.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
        frame.setFacingDirection(BlockFace.UP);
        // set frame's item
        ItemStack pipe = new ItemStack(Material.STRING);
        ItemMeta im = pipe.getItemMeta();
        im.setDisplayName("");
        im.setCustomModelData(Pipe.STRAIGHT.getCustomeModelData());
        im.getPersistentDataContainer().set(Ores.getPipeKey(), PersistentDataType.INTEGER, 1);
        pipe.setItemMeta(im);
        frame.setItem(pipe);
        frame.setRotation(getRotation(player.getFacing()));
        frame.setVisible(false);
    }
}
