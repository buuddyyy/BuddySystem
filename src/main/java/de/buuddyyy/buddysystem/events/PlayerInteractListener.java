package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
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
            new FastHarvestBlock(Material.WHEAT, new FastHarvestBlock.HarvestDrop[]{
                    new FastHarvestBlock.HarvestDrop(Material.WHEAT, 1, 2),
                    new FastHarvestBlock.HarvestDrop(Material.WHEAT_SEEDS, 1, 3)
            }),
            new FastHarvestBlock(Material.CARROTS, Material.CARROT, 2, 4),
            new FastHarvestBlock(Material.POTATOES, Material.POTATO, 2, 4),
            new FastHarvestBlock(Material.BEETROOTS, new FastHarvestBlock.HarvestDrop[]{
                    new FastHarvestBlock.HarvestDrop(Material.BEETROOT, 1, 4),
                    new FastHarvestBlock.HarvestDrop(Material.BEETROOT_SEEDS, 2, 3)
            })
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
                && !event.hasItem()
                && (fastHarvestBlock = getHarvestableBlock(event.getClickedBlock().getType())) != null) {
            event.setCancelled(true);
            var block = event.getClickedBlock();
            if (!(block.getBlockData() instanceof Ageable a)) {
                return;
            }
            if (a.getAge() < a.getMaximumAge()) {
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

        for (var harvestDrop : fastHarvestBlock.harvestDrops) {
            int minDrop = harvestDrop.minDrop;
            int maxDrop = harvestDrop.maxDrop;
            int dropAmount = random.nextInt(maxDrop - minDrop) + minDrop;
            w.dropItemNaturally(loc, new ItemStack(harvestDrop.dropMaterial, dropAmount));
        }
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
        private final HarvestDrop[] harvestDrops;

        public FastHarvestBlock(Material harvestMaterial, Material dropMaterial, int minDrop, int maxDrop) {
            this(harvestMaterial, new HarvestDrop[] {
                    new HarvestDrop(dropMaterial, minDrop, maxDrop)
            });
        }

        public FastHarvestBlock(Material harvestMaterial, HarvestDrop[] harvestDrops) {
            this.harvestMaterial = harvestMaterial;
            this.harvestDrops = harvestDrops;
        }

        private record HarvestDrop(Material dropMaterial, int minDrop, int maxDrop) {

        }

    }

}
