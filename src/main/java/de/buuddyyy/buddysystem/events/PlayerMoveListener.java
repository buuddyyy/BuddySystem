package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.TeleportHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final BuddySystemPlugin plugin;

    public PlayerMoveListener(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final var p = event.getPlayer();
        if (plugin.getTeleportHandler().handleMove(event) != TeleportHandler.EnumPlayerTeleportStatus.OK) {
            p.sendMessage(this.plugin.getPrefix() + "§cDu hast dich bewegt. Teleportation wurde abgebrochen!");
        }
    }

}
