package me.eccentric_nz.ores.nuclear;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class NuclearStorage {

    private static final NuclearStorage INSTANCE = new NuclearStorage();
    private static List<Block> blocks;
    public Connection connection = null;
    public Statement statement;

    public static synchronized NuclearStorage getInstance() {
        return INSTANCE;
    }

    public static void loadBlocks() {
        Bukkit.getLogger().log(Level.INFO, "[mOre] Loading nuclear blocks...");
        List<Block> blocks = new ArrayList<>();
        // get blocks from database
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = getInstance().connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM nuclear_blocks");
            while (rs.next()) {
                World world = Bukkit.getWorld(rs.getString("world"));
                if (world != null) {
                    Block block = world.getBlockAt(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                    blocks.add(block);
                } else {
                    Bukkit.getLogger().log(Level.INFO, "world is null!");
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not get nuclear blocks: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                Bukkit.getLogger().log(Level.WARNING, "Could not close result set/statement when getting nuclear blocks: " + e);
            }
        }
        NuclearStorage.blocks = blocks;
    }

    public static void saveBlock(Block block) {
        try {
            String querySave = "INSERT INTO nuclear_blocks (world, x, y, z) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = getInstance().connection.prepareStatement(querySave);
            ps.setString(1, block.getWorld().getName());
            ps.setInt(2, block.getX());
            ps.setInt(3, block.getY());
            ps.setInt(4, block.getZ());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not save nuclear block: " + e);
        }
    }

    public static void addBlock(Block block) {
        saveBlock(block);
        blocks.add(block);
    }

    public static void deleteBlock(Block block) {
        PreparedStatement ps = null;
        try {
            ps = getInstance().connection.prepareStatement("DELETE FROM nuclear_blocks WHERE world = ? AND x = ? AND y = ? AND z = ?");
            ps.setString(1, block.getWorld().getName());
            ps.setInt(2, block.getX());
            ps.setInt(3, block.getY());
            ps.setInt(4, block.getZ());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not delete nuclear block: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                Bukkit.getLogger().log(Level.WARNING, "Could not close result set/statement when getting nuclear blocks: " + e);
            }
        }
    }

    public static void removeBlock(Block block) {
        deleteBlock(block);
        blocks.remove(block);
    }

    public static List<Block> getBlocks() {
        return blocks;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(String path) throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
    }

    public void createTable() {
        try {
            statement = connection.createStatement();
            String queryNuclear = "CREATE TABLE IF NOT EXISTS nuclear_blocks (block_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, world TEXT, x INTEGER, y INTEGER, z INTEGER)";
            statement.executeUpdate(queryNuclear);
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Create table error: " + e);
        }
    }
}
