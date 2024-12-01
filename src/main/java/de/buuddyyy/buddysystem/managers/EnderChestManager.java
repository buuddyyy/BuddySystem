package de.buuddyyy.buddysystem.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.sql.DatabaseManager;
import de.buuddyyy.buddysystem.sql.entities.EnderChestEntity;
import de.buuddyyy.buddysystem.sql.entities.PlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class EnderChestManager {

    public static final String TABLE_NAME = "player_ender_chests";

    private final static Logger LOGGER = Logger.getLogger(EnderChestManager.class.getName());

    private final LoadingCache<UUID, EnderChestEntity> playerEnderChests;
    private final BuddySystemPlugin plugin;
    private final DatabaseManager databaseManager;

    public EnderChestManager(BuddySystemPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.playerEnderChests = CacheBuilder.newBuilder().build(new CacheLoader<>() {
            @Override
            public EnderChestEntity load(UUID uuid) {
                return loadEnderChest(uuid);
            }
        });
    }

    public void createEnderChest(EnderChestEntity entity) {
        final var uuid = entity.getPlayerEntity().getPlayerUuid();
        if (hasEnderChest(uuid))
            return;
        this.databaseManager.insertEntity(entity);
        this.playerEnderChests.refresh(uuid);
    }

    public void updateEnderChest(EnderChestEntity entity) {
        final var uuid = entity.getPlayerEntity().getPlayerUuid();
        this.databaseManager.updateEntity(entity);
        this.playerEnderChests.refresh(uuid);
    }

    public boolean hasEnderChest(UUID uuid) {
        return this.playerEnderChests.asMap().containsKey(uuid);
    }

    public EnderChestEntity getEnderChest(UUID uuid) {
        try {
            return this.playerEnderChests.get(uuid);
        } catch (ExecutionException ex) {
            throw new IllegalStateException("Player has not a Ender Chest", ex);
        }
    }

    private EnderChestEntity loadEnderChest(UUID uuid) {
        final String sql = "SELECT * FROM %s WHERE playerId=:playerId";
        final Map<String, Object> parameters = Maps.newHashMap();
        final PlayerEntity pe = plugin.getPlayerHandler().getPlayerEntity(uuid);
        parameters.put("playerId", pe.getId());
        return databaseManager.queryResult(EnderChestEntity.class,
                String.format(sql, TABLE_NAME), parameters);
    }

}
