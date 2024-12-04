package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlayerInteractListener implements Listener {

    private final Random random = new Random();

    private final BuddySystemPlugin plugin;

    private static final List<FastHarvestBlock> HARVESTABLE_BLOCKS = Arrays.asList(
            new FastHarvestBlock(Material.WHEAT, Material.WHEAT_SEEDS, 7, 1, 2),
            new FastHarvestBlock(Material.CARROT, 7, 0,1),
            new FastHarvestBlock(Material.POTATO, 7, 0,1)
    );

    public PlayerInteractListener(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        this.plugin.getEnderChestHandler().handlePlayerInteract(event);

        FastHarvestBlock fastHarvestBlock;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock() != null
                && (fastHarvestBlock = getHarvestableBlock(event.getClickedBlock().getType())) != null) {
            event.setCancelled(true);
            var block = event.getClickedBlock();
            if (block.getData() < (byte) fastHarvestBlock.maxAge) {
                return;
            }
            dropHarvest(fastHarvestBlock, event.getClickedBlock());
            return;
        }

    }

    private void dropHarvest(FastHarvestBlock fastHarvestBlock, Block block) {
        block.setType(fastHarvestBlock.harvestMaterial);
        var w = block.getWorld();
        var loc = block.getLocation();
        w.dropItemNaturally(loc, new ItemStack(fastHarvestBlock.harvestMaterial));
        int dropAmount = random.nextInt(fastHarvestBlock.maxDrop) + fastHarvestBlock.minDrop;
        w.dropItemNaturally(loc, new ItemStack(fastHarvestBlock.dropMaterial, dropAmount));
    }

    private FastHarvestBlock getHarvestableBlock(Material material) {
        for (FastHarvestBlock block : HARVESTABLE_BLOCKS) {
            if (block.harvestMaterial == material) {
                return block;
            }
        }
        return null;
    }

    private static final class FastHarvestBlock {

        private final Material harvestMaterial;
        private final Material dropMaterial;
        private final int maxAge;
        private final int minDrop;
        private final int maxDrop;

        public FastHarvestBlock(Material harvestMaterial, int magAge, int minDrop, int maxDrop) {
            this(harvestMaterial, harvestMaterial, magAge, minDrop, maxDrop);
        }

        public FastHarvestBlock(Material harvestMaterial, Material dropMaterial, int maxAge, int minDrop, int maxDrop) {
            this.harvestMaterial = harvestMaterial;
            this.dropMaterial = dropMaterial;
            this.maxAge = maxAge;
            this.minDrop = minDrop;
            this.maxDrop = maxDrop;
        }

    }

}
