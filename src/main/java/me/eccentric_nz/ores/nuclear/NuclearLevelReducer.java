package me.eccentric_nz.ores.nuclear;

import me.eccentric_nz.ores.mOre;
import me.eccentric_nz.ores.pipe.CustomBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Levelled;
import org.bukkit.persistence.PersistentDataType;

public class NuclearLevelReducer implements Runnable {

    private final NuclearData data;
    private final int level;
    private int task;

    public NuclearLevelReducer(NuclearData data, int level) {
        this.data = data;
        this.level = level;
    }

    @Override
    public void run() {
        if (data.getWater().getType().equals(Material.WATER_CAULDRON)) {
            Levelled levelled = (Levelled) data.getWater().getBlockData();
            if (levelled.getLevel() > 1) {
                levelled.setLevel(levelled.getLevel() - 1);
                data.getWater().setBlockData(levelled);
            } else {
                Bukkit.getScheduler().cancelTask(task);
                data.getWater().setType(Material.CAULDRON);
                CustomBlockData customBlockData = new CustomBlockData(data.getGenerator(), mOre.getPlugin());
                customBlockData.set(mOre.getWetKey(), PersistentDataType.INTEGER, 0);
            }
        } else {
            Bukkit.getScheduler().cancelTask(task);
            CustomBlockData customBlockData = new CustomBlockData(data.getGenerator(), mOre.getPlugin());
            customBlockData.set(mOre.getWetKey(), PersistentDataType.INTEGER, 0);
        }
    }

    public void setTask(int task) {
        this.task = task;
    }
}
