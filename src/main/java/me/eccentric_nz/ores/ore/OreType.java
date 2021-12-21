package me.eccentric_nz.ores.ore;

import org.bukkit.Material;

import java.util.HashMap;

public enum OreType {

    ALUMINIUM(Material.RAW_IRON, "Raw Bauxite", "Aluminium Ingot"),
    URANIUM(Material.RAW_COPPER, "Raw Uranium", "Uranium Pellet"),
    LEAD(Material.RAW_GOLD, "Lead Glance", "Lead Ingot");

    private static final HashMap<Material, OreType> BY_MATERIAL = new HashMap<>();

    static {
        for (OreType ore : values()) {
            BY_MATERIAL.put(ore.getMaterial(), ore);
        }
    }

    private final Material material;
    private final String oreName;
    private final String ingotName;

    OreType(Material material, String oreName, String ingotName) {
        this.material = material;
        this.oreName = oreName;
        this.ingotName = ingotName;
    }

    public static OreType getByMaterial(Material material) {
        return BY_MATERIAL.get(material);
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
}
