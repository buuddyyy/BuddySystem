package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    private final BuddySystemPlugin plugin;

    public InventoryCloseListener(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        this.plugin.getEnderChestHandler().handleInventoryClose(event);
    }

}
