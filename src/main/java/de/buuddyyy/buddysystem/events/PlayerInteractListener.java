package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final BuddySystemPlugin plugin;

    public PlayerInteractListener(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        this.plugin.getEnderChestHandler().handlePlayerInteract(event);
    }

}
