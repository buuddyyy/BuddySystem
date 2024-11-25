package de.buuddyyy.buddysystem.handlers;

import com.google.common.collect.Maps;
import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.managers.ChestLockManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public class ChestLockHandler {

    private static final Map<UUID, BukkitTask> PLAYER_ACTIONBAR_TASK = Maps.newHashMap();

    private static final Material[] WHITELIST_MATERIALS = new Material[] {
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.FURNACE,
            Material.BARREL,
            Material.DROPPER,
            Material.DISPENSER,
            Material.HOPPER
    };

    private final BuddySystemPlugin plugin;
    private final ChestLockManager chestLockManager;

    public ChestLockHandler(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.chestLockManager = new ChestLockManager(plugin.getDatabaseManager());
    }

    public EnumChestLockStatus handlePlayerInteraction(PlayerInteractEvent event) {
        return EnumChestLockStatus.OK;
    }

    public EnumChestLockStatus handleBlockPlace(BlockPlaceEvent event) {
        return EnumChestLockStatus.OK;
    }

    public EnumChestLockStatus handleBlockBreak(BlockBreakEvent event) {
        return EnumChestLockStatus.OK;
    }

    private boolean isInWhitelist(Block block) {
        return this.isInWhitelist(block.getType());
    }

    private boolean isInWhitelist(Material material) {
        for (Material m : WHITELIST_MATERIALS) {
            if (material == m) {
                return true;
            }
        }
        return false;
    }

    public enum EnumChestLockAction {
        CREATE_ONCE,
        CREATE_MULTI,
        REMOVE_ONCE,
        REMOVE_MULTI,
        TRUST_PLAYER,
        UNTRUST_PLAYER,
        SHOW_INFO
    }

    public enum EnumChestLockStatus {
        OK,
        ALREADY_LOCKED,
        NOT_OWNER,
        UNKNOWN_ERROR
    }

}
