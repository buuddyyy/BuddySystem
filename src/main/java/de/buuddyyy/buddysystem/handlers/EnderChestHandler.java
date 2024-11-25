package de.buuddyyy.buddysystem.handlers;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.managers.EnderChestManager;
import de.buuddyyy.buddysystem.sql.entities.EnderChestEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class EnderChestHandler {

    public static final int INVENTORY_SIZE = 9*6;

    private final BuddySystemPlugin plugin;
    private final EnderChestManager enderChestManager;

    public EnderChestHandler(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.enderChestManager = new EnderChestManager(plugin, this.plugin.getDatabaseManager());
    }

    public void openEnderChest(Player player) {
        this.openEnderChest(player, player);
    }

    public void openEnderChest(Player player, Player targetPlayer) {
        if (!this.enderChestManager.hasEnderChest(targetPlayer.getUniqueId())) {
            integrateEnderChest(targetPlayer.getPlayer());
        }
        var isOwnEnderChest = player.equals(targetPlayer);
        var invName = isOwnEnderChest ? "Deine EnderChest"
                : ("EnderChest von " + targetPlayer.getName());
        var inv = (Inventory) Bukkit.createInventory(null, INVENTORY_SIZE, invName);
        var ece = this.enderChestManager.getEnderChest(targetPlayer.getUniqueId());
        var targetInv = ece.getInventory();
        for (int i = 0; i < targetInv.getSize(); i++) {
            inv.setItem(i, targetInv.getItem(i));
        }
        player.openInventory(inv);
    }

    public void saveInventory(Player targetPlayer, Inventory inventory) {
        if (!this.enderChestManager.hasEnderChest(targetPlayer.getUniqueId())) {
            integrateEnderChest(targetPlayer.getPlayer());
        }
        final var newInv = Bukkit.createInventory(null, INVENTORY_SIZE);
        for (int i = 0; i < inventory.getSize(); i++) {
            newInv.setItem(i, inventory.getItem(i));
        }
        var ece = this.enderChestManager.getEnderChest(targetPlayer.getUniqueId());
        ece.setInventory(newInv);
        this.enderChestManager.updateEnderChest(ece);
    }

    public void handlePlayerJoin(Player player) {
        if (this.enderChestManager.hasEnderChest(player.getUniqueId()))
            return;
        integrateEnderChest(player);
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
        if (isOwnEnderChest) {
            this.saveInventory(p, inv);
        }
    }

    private void integrateEnderChest(Player player) {
        var integrated = this.canIntegrateEnderChest(player);
        if (integrated) {
            System.out.printf("EnderChest from Player %s can be integrated\n", player.getName());
        }
    }

    private boolean canIntegrateEnderChest(Player player) {
        var legacyEnderChest = player.getEnderChest();
        if (enderChestManager.hasEnderChest(player.getUniqueId())) {
            return false;
        }
        var inv = Bukkit.createInventory(null, INVENTORY_SIZE);
        for (int i = 0; i < legacyEnderChest.getSize(); i++) {
            inv.setItem(i, legacyEnderChest.getItem(i));
        }
        legacyEnderChest.clear();
        var pe = this.plugin.getPlayerHandler().getPlayerEntity(player);
        var entity = new EnderChestEntity(pe, inv);
        this.enderChestManager.createEnderChest(entity);
        return true;
    }

}
