/*
 * Made by mfnalex / JEFF Media GbR
 *
 * If you find this helpful or if you're using this project inside your paid plugins,
 * consider leaving a donation :)
 *
 * https://paypal.me/mfnalex
 *
 * If you need help or have any suggestions, just create an issue or join my discord
 * and head to the channel #programming-help
 *
 * https://discord.jeff-media.de
 */

package me.eccentric_nz.ores.pipe;

import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * PersistentDataContainer for blocks!
 * <p>
 * Blocks do not implement PersistentDataHolder, so you cannot store custom data inside them.
 * We can however just use the chunk instead. Every Block gets its own PersistentDataContainer,
 * created and stored only when needed.
 * <p>
 * Once you clear the custom data of a block, the PersistentDataContainer also gets removed
 * from the chunk's PersistentDataContainer.
 * <p>
 * The basic idea is extremely simple - every block's PersistentDataContainer is stored as
 * {@link PersistentDataType#TAG_CONTAINER} inside the chunk.
 * The {@link NamespacedKey} used inside the chunk's container is linked to the block's
 * relative coordinates inside the chunk. That's basically it^^
 */
@SuppressWarnings("NullableProblems")
public class CustomBlockData implements PersistentDataContainer {

    private final PersistentDataContainer pdc;
    private final Chunk chunk;
    private final NamespacedKey key;

    /**
     * Gets the PersistentDataContainer associated with the given block and plugin
     *
     * @param block  Block
     * @param plugin Plugin
     */
    public CustomBlockData(final Block block, final Plugin plugin) {
        this.chunk = block.getChunk();
        this.key = new NamespacedKey(plugin, getKey(block));
        this.pdc = getPersistentDataContainer();
    }

    /**
     * Gets the PersistentDataContainer associated with the given block and plugin
     *
     * @param block     Block
     * @param namespace Namespace
     * @deprecated Use {@link #CustomBlockData(Block, Plugin)} instead.
     */
    @Deprecated()
    public CustomBlockData(final Block block, final String namespace) {
        this.chunk = block.getChunk();
        this.key = new NamespacedKey(namespace, getKey(block));
        this.pdc = getPersistentDataContainer();
    }

    /**
     * Gets a NamespacedKey that consists of the block's relative coordinates within its chunk
     *
     * @param block block
     * @return NamespacedKey consisting of the block's relative coordinates within its chunk
     */

    private static String getKey(Block block) {
        final int x = block.getX() & 0x000F;
        final int y = block.getY() & 0x00FF;
        final int z = block.getZ() & 0x000F;
        return String.format("x%dy%dz%d", x, y, z);
    }

    /**
     * Removes all custom block data
     */
    public void clear() {
        if (pdc.has(key, PersistentDataType.TAG_CONTAINER)) {
            pdc.remove(key);
        }
        save();
    }

    /**
     * Gets the PersistentDataContainer associated with this block.
     *
     * @return PersistentDataContainer of this block
     */

    private PersistentDataContainer getPersistentDataContainer() {
        final PersistentDataContainer chunkPDC = chunk.getPersistentDataContainer();
        final PersistentDataContainer blockPDC;
        if (chunkPDC.has(key, PersistentDataType.TAG_CONTAINER)) {
            blockPDC = chunkPDC.get(key, PersistentDataType.TAG_CONTAINER);
            assert blockPDC != null;
            return blockPDC;
        }
        blockPDC = chunkPDC.getAdapterContext().newPersistentDataContainer();
        chunkPDC.set(key, PersistentDataType.TAG_CONTAINER, blockPDC);
        return blockPDC;
    }

    /**
     * Saves the block's PersistentDataContainer inside the chunk's PersistentDataContainer
     */
    private void save() {
        if (pdc.isEmpty()) {
            chunk.getPersistentDataContainer().remove(key);
        } else {
            chunk.getPersistentDataContainer().set(key, PersistentDataType.TAG_CONTAINER, pdc);
        }
    }

    @Override
    public <T, Z> void set(final NamespacedKey namespacedKey, final PersistentDataType<T, Z> persistentDataType, final Z z) {
        pdc.set(namespacedKey, persistentDataType, z);
        save();
    }

    @Override
    public <T, Z> boolean has(final NamespacedKey namespacedKey, final PersistentDataType<T, Z> persistentDataType) {
        return pdc.has(namespacedKey, persistentDataType);
    }


    @Override
    public <T, Z> Z get(final NamespacedKey namespacedKey, final PersistentDataType<T, Z> persistentDataType) {
        return pdc.get(namespacedKey, persistentDataType);
    }


    @Override
    public <T, Z> Z getOrDefault(final NamespacedKey namespacedKey, final PersistentDataType<T, Z> persistentDataType, final Z z) {
        return pdc.getOrDefault(namespacedKey, persistentDataType, z);
    }


    @Override
    public Set<NamespacedKey> getKeys() {
        return pdc.getKeys();
    }

    @Override
    public void remove(final NamespacedKey namespacedKey) {
        pdc.remove(namespacedKey);
        save();
    }

    @Override
    public boolean isEmpty() {
        return pdc.isEmpty();
    }


    @Override
    public PersistentDataAdapterContext getAdapterContext() {
        return pdc.getAdapterContext();
    }
}

