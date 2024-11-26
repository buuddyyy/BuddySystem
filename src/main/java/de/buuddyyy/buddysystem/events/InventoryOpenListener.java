package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryOpenListener implements Listener {

    private final BuddySystemPlugin plugin;

    public InventoryOpenListener(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

    }

}
