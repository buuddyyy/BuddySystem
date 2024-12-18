package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final BuddySystemPlugin plugin;

    public PlayerJoinListener(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        plugin.getPlayerHandler().handleJoin(p);
    }

}
