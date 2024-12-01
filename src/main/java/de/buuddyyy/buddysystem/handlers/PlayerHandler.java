package de.buuddyyy.buddysystem.handlers;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.managers.PlayerManager;
import de.buuddyyy.buddysystem.sql.entities.PlayerEntity;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class PlayerHandler {

    private final BuddySystemPlugin plugin;
    private final PlayerManager playerManager;

    public PlayerHandler(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.playerManager = new PlayerManager(plugin.getDatabaseManager());
    }

    public void handlePlayerJoin(Player player) {
        if (!player.hasPlayedBefore()) {
            plugin.getSpawnHandler().teleportPlayerToSpawnImmediately(player);
        }
        if (!playerManager.playerExists(player)) {
            playerManager.createPlayerEntity(player);
            return;
        }
        final PlayerEntity pe = playerManager.getPlayerEntity(player.getUniqueId());
        plugin.getHomeHandler().getHomeNames(player);
        if (player.getName().equals(pe.getPlayerName())) {
            return;
        }
        pe.setPlayerName(player.getName());
        this.playerManager.updatePlayerEntity(pe);
    }

    public void handleQuit(Player player) {
        if (!playerManager.playerExists(player))
            return;
        final PlayerEntity pe = this.playerManager.getPlayerEntity(player.getUniqueId());
        pe.setLastOnline(Timestamp.from(Instant.now()));
        this.playerManager.updatePlayerEntity(pe);
    }

    public boolean playerExists(Player player) {
        return this.playerManager.playerExists(player);
    }

    public PlayerEntity getPlayerEntity(Player player) {
        return this.playerManager.getPlayerEntity(player.getUniqueId());
    }

    public PlayerEntity getPlayerEntity(UUID playerUuid) {
        return this.playerManager.getPlayerEntity(playerUuid);
    }

    public PlayerEntity getPlayerEntityByName(String playerName) {
        return this.playerManager.getPlayerByName(playerName);
    }

}
