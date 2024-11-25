package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class PlayerBedEnterListener implements Listener {

    private final BuddySystemPlugin plugin;

    public PlayerBedEnterListener(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        final Player p = e.getPlayer();
        if (e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            this.plugin.getSkipNightHandler().handleBedEnter(p);
        }

    }

}
