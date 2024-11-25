package de.buuddyyy.buddysystem.handlers;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.managers.HomeManager;
import de.buuddyyy.buddysystem.sql.entities.HomeEntity;
import de.buuddyyy.buddysystem.sql.entities.PlayerEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeHandler {

    private final BuddySystemPlugin plugin;
    private final HomeManager homeManager;

    public HomeHandler(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.homeManager = new HomeManager(plugin, plugin.getDatabaseManager());
    }

    public EnumPlayerHomeStatus teleportPlayerToHome(Player player, String homeName) {
        if (!existsPlayerHome(player, homeName)) {
            return EnumPlayerHomeStatus.NOT_EXISTS;
        }
        final Map<String, HomeEntity> map = this.homeManager.getHomes(player.getUniqueId());
        final HomeEntity homeEntity = map.get(homeName);
        TeleportHandler.teleportPlayer(player, homeEntity.getLocation());
        return EnumPlayerHomeStatus.OK;
    }

    public EnumPlayerHomeStatus setPlayerHome(Player player, String homeName, Location location) {
        final var playerHandler = this.plugin.getPlayerHandler();
        if (!playerHandler.playerExists(player)) {
            return EnumPlayerHomeStatus.UNKNOWN_ERROR;
        }
        final Map<String, HomeEntity> map = this.homeManager.getHomes(player.getUniqueId());
        final PlayerEntity pe = playerHandler.getPlayerEntity(player);
        EnumPlayerHomeStatus status;
        if (map.containsKey(homeName)) {
            HomeEntity homeEntity = map.get(homeName);
            homeEntity.setLocation(location);
            this.plugin.getDatabaseManager().updateEntity(homeEntity);
            status = EnumPlayerHomeStatus.OK_OVERWRITTEN;
        } else {
            HomeEntity homeEntity = new HomeEntity(pe, homeName, location);
            this.plugin.getDatabaseManager().insertEntity(homeEntity);
            status = EnumPlayerHomeStatus.OK;
        }
        this.homeManager.getPlayerHomes().refresh(player.getUniqueId());
        return status;
    }

    public EnumPlayerHomeStatus deleteHome(Player player, String homeName) {
        final var playerHandler = this.plugin.getPlayerHandler();
        if (!playerHandler.playerExists(player)) {
            return EnumPlayerHomeStatus.UNKNOWN_ERROR;
        }
        final Map<String, HomeEntity> map = this.homeManager.getHomes(player.getUniqueId());
        if (!map.containsKey(homeName)) {
            return EnumPlayerHomeStatus.NOT_EXISTS;
        }
        HomeEntity he = map.get(homeName);
        this.plugin.getDatabaseManager().deleteEntity(he);
        this.homeManager.getPlayerHomes().refresh(player.getUniqueId());
        return EnumPlayerHomeStatus.OK;

    }

    public List<String> getHomeNames(Player player) {
        return new ArrayList<>(this.homeManager.getHomes(player.getUniqueId()).keySet());
    }

    private boolean existsPlayerHome(Player player, String homeName) {
        return this.homeManager.getHomes(player.getUniqueId()).containsKey(homeName);
    }

    public enum EnumPlayerHomeStatus {
        OK,
        OK_OVERWRITTEN,
        NOT_EXISTS,
        UNKNOWN_ERROR
    }

}
