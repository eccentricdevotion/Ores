package me.eccentric_nz.ores.nuclear;

import org.bukkit.block.Block;

public class NuclearRunnable implements Runnable {

    @Override
    public void run() {
        // if generator is on, reduce the pellet count every minecraft day (24000 ticks)
        for (Block block : NuclearStorage.getBlocks()) {
            int amount = NuclearInventory.getAmount(block);
            if (amount > 0) {
                NuclearInventory.setAmount(block, amount - 1);
            }
        }
    }
}
