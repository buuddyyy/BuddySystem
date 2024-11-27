package de.buuddyyy.buddysystem.handlers;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.managers.EnderChestManager;
import de.buuddyyy.buddysystem.sql.entities.EnderChestEntity;
import de.buuddyyy.buddysystem.sql.entities.PlayerEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class EnderChestHandler {

    public static final int INVENTORY_SIZE = 9*6;
    public static final String METADATA_NAME = "viewing_enderchest";

    private final BuddySystemPlugin plugin;
    private final EnderChestManager enderChestManager;
    private final PlayerHandler playerHandler;

    public EnderChestHandler(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.enderChestManager = new EnderChestManager(plugin, this.plugin.getDatabaseManager());
        this.playerHandler = plugin.getPlayerHandler();
    }

    public void openEnderChest(Player player) {
        var pe = this.playerHandler.getPlayerEntity(player);
        this.openEnderChest(player, pe);
    }

    public EnumEnderChestStatus openEnderChest(Player player, String targetPlayerName) {
        var targetPe = this.playerHandler.getPlayerEntityByName(targetPlayerName);
        if (targetPe == null) {
            return EnumEnderChestStatus.PLAYER_NOT_EXISTS;
        }
        if (!this.enderChestManager.hasEnderChest(targetPe.getPlayerUuid())) {
            return EnumEnderChestStatus.PLAYER_HAS_NOT_ENDERCHEST;
        }
        player.setMetadata(METADATA_NAME, new FixedMetadataValue(this.plugin,
                targetPe.getPlayerUuid().toString()));
        return this.openEnderChest(player, targetPe);
    }

    private EnumEnderChestStatus openEnderChest(Player player, PlayerEntity targetPlayer) {
        var isOwnEnderChest = player.getName().equals(targetPlayer.getPlayerName());
        var invName = isOwnEnderChest ? "Deine EnderChest"
                : ("EnderChest von " + targetPlayer.getPlayerName());
        var inv = (Inventory) Bukkit.createInventory(null, INVENTORY_SIZE, invName);
        var ece = this.enderChestManager.getEnderChest(targetPlayer.getPlayerUuid());
        var targetInv = ece.getInventory();
        for (int i = 0; i < targetInv.getSize(); i++) {
            inv.setItem(i, targetInv.getItem(i));
        }
        player.openInventory(inv);
        return EnumEnderChestStatus.OK;
    }

    public void saveInventory(PlayerEntity targetPlayer, Inventory inventory) {
        if (!this.enderChestManager.hasEnderChest(targetPlayer.getPlayerUuid())) {
            return;
        }
        final var newInv = Bukkit.createInventory(null, INVENTORY_SIZE);
        for (int i = 0; i < inventory.getSize(); i++) {
            newInv.setItem(i, inventory.getItem(i));
        }
        var ece = this.enderChestManager.getEnderChest(targetPlayer.getPlayerUuid());
        ece.setInventory(newInv);
        this.enderChestManager.updateEnderChest(ece);
    }

    public void handlePlayerJoin(Player player) {
        if (this.enderChestManager.hasEnderChest(player.getUniqueId())) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () ->
                this.integrateEnderChest(player), 2L);
    }

    public void handlePlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getClickedBlock().getType() != Material.ENDER_CHEST) {
            return;
        }

        event.setCancelled(true);
        this.openEnderChest(event.getPlayer());
    }

    public void handleInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player p)) {
            return;
        }

        final var inv = event.getInventory();
        if (inv.getHolder() != null)
            return;

        var inventoryName = p.getOpenInventory().getTitle();
        var isOwnEnderChest = "Deine EnderChest".equals(inventoryName);

        if (isOwnEnderChest && !p.hasMetadata(METADATA_NAME)) {
            this.saveInventory(playerHandler.getPlayerEntity(p), inv);
            return;
        }

        final var metadatas = p.getMetadata(METADATA_NAME);
        if (metadatas.isEmpty())
            return;

        var targetUuidString = String.valueOf(metadatas.getFirst().value());
        var targetUuid = UUID.fromString(targetUuidString);
        this.saveInventory(playerHandler.getPlayerEntity(targetUuid), inv);
    }

    private void integrateEnderChest(Player player) {
        var legacyEnderChest = player.getEnderChest();
        if (enderChestManager.hasEnderChest(player.getUniqueId())) {
            return;
        }
        var inv = Bukkit.createInventory(null, INVENTORY_SIZE);
        for (int i = 0; i < legacyEnderChest.getSize(); i++) {
            inv.setItem(i, legacyEnderChest.getItem(i));
        }
        legacyEnderChest.clear();
        var pe = this.playerHandler.getPlayerEntity(player);
        var entity = new EnderChestEntity(pe, inv);
        this.enderChestManager.createEnderChest(entity);
    }

    public enum EnumEnderChestStatus {
        OK,
        PLAYER_NOT_EXISTS,
        PLAYER_HAS_NOT_ENDERCHEST
    }

}
