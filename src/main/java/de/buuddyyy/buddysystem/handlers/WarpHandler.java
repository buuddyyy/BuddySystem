package de.buuddyyy.buddysystem.handlers;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.managers.WarpManager;
import de.buuddyyy.buddysystem.sql.entities.WarpEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpHandler {

    private final BuddySystemPlugin plugin;
    private final WarpManager warpManager;

    public WarpHandler(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.warpManager = new WarpManager(this.plugin.getDatabaseManager());
        this.warpManager.loadWarps();
    }

    public EnumPlayerWarpStatus teleportPlayerToWarp(Player player, String warpName) {
        if (!existsWarp(warpName)) {
            return EnumPlayerWarpStatus.NOT_EXISTS;
        }
        WarpEntity we = this.warpManager.getWarp(warpName);
        TeleportHandler.teleportPlayer(player, we.getLocation());
        return EnumPlayerWarpStatus.OK;
    }

    public EnumPlayerWarpStatus setWarp(Player player, String warpName, Location location) {
        final var playerHandler = this.plugin.getPlayerHandler();
        if (!playerHandler.playerExists(player)) {
            return EnumPlayerWarpStatus.UNKNOWN_ERROR;
        }
        final var pe = playerHandler.getPlayerEntity(player);
        EnumPlayerWarpStatus status;
        if (existsWarp(warpName)) {
            WarpEntity warpEntity = this.warpManager.getWarp(warpName);
            if (warpEntity.getPlayerEntity().getId() != pe.getId()) {
                if (!player.isOp()) {
                    return EnumPlayerWarpStatus.NOT_OWNER;
                } else {
                    warpEntity.setPlayerEntity(pe);
                }
            }
            warpEntity.setLocation(location);
            this.warpManager.updateWarp(warpEntity);
            status = EnumPlayerWarpStatus.OK_OVERWRITTEN;
        } else {
            WarpEntity warpEntity = new WarpEntity(pe, warpName, location);
            this.warpManager.createWarp(warpEntity);
            status = EnumPlayerWarpStatus.OK;
        }
        return status;
    }

    public EnumPlayerWarpStatus deleteWarp(Player player, String warpName) {
        final var playerHandler = this.plugin.getPlayerHandler();
        if (!playerHandler.playerExists(player)) {
            return EnumPlayerWarpStatus.UNKNOWN_ERROR;
        }
        if (!existsWarp(warpName)) {
            return EnumPlayerWarpStatus.NOT_EXISTS;
        }
        final var pe = playerHandler.getPlayerEntity(player);
        final var warpEntity = this.warpManager.getWarp(warpName);
        if (warpEntity.getPlayerEntity().getId() != pe.getId()) {
            return EnumPlayerWarpStatus.NOT_OWNER;
        }
        this.warpManager.deleteWarp(warpEntity);
        return EnumPlayerWarpStatus.OK;
    }

    public List<String> getWarpNames() {
        return this.warpManager.getWarpNames();
    }

    private boolean existsWarp(String warpName) {
        return this.warpManager.getWarp(warpName) != null;
    }

    public enum EnumPlayerWarpStatus {
        OK,
        OK_OVERWRITTEN,
        NOT_EXISTS,
        NOT_OWNER,
        UNKNOWN_ERROR
    }

}
