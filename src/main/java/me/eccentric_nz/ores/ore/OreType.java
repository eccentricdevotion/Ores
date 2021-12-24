package me.eccentric_nz.ores.ore;

import me.eccentric_nz.ores.mOre;
import org.bukkit.Material;
import org.bukkit.block.data.MultipleFacing;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public enum OreType {

    ALUMINIUM(Material.RAW_IRON, "Raw Bauxite", "Aluminium Ingot", OreData.bauxiteMushroom),
    URANIUM(Material.RAW_COPPER, "Raw Uranium", "Uranium Pellet", OreData.uraniumMushroom),
    LEAD(Material.RAW_GOLD, "Lead Glance", "Lead Ingot", OreData.leadMushroom);

    private static final HashMap<Material, OreType> BY_MATERIAL = new HashMap<>();

    static {
        for (OreType ore : values()) {
            BY_MATERIAL.put(ore.getMaterial(), ore);
        }
    }

    private final Material material;
    private final String oreName;
    private final String ingotName;
    private final MultipleFacing data;

    OreType(Material material, String oreName, String ingotName, MultipleFacing data) {
        this.material = material;
        this.oreName = oreName;
        this.ingotName = ingotName;
        this.data = data;
    }

    public static OreType getByMaterial(Material material) {
        return BY_MATERIAL.get(material);
    }

    public MultipleFacing getData() {
        return data;
    }

    public Material getMaterial() {
        return material;
    }

    public String getOreName() {
        return oreName;
    }

    public String getIngotName() {
        return ingotName;
    }

    public int getDropCount() {
        String key = this.toString().toLowerCase(Locale.ROOT);
        int min = mOre.getPlugin().getConfig().getInt("ores." + key + ".drops.min");
        int max = mOre.getPlugin().getConfig().getInt("ores." + key + ".drops.max");
        return ThreadLocalRandom.current().nextInt(max - min) + min;
    }
}
