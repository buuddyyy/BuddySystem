package de.buuddyyy.buddysystem.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.buuddyyy.buddysystem.sql.DatabaseManager;
import de.buuddyyy.buddysystem.sql.entities.chestlock.ChestLockEntity;
import org.bukkit.Location;

import java.util.HashMap;

public class ChestLockManager {

    public static final String TABLE_NAME_CHEST_LOCKS = "chestlocks";
    public static final String TABLE_NAME_TRUSTED_PLAYERS = "chestlocks_trusted_players";

    private final LoadingCache<Location, ChestLockEntity> chestLockEntities;
    private final DatabaseManager databaseManager;

    public ChestLockManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.chestLockEntities = CacheBuilder.newBuilder().build(new CacheLoader<>() {
            @Override
            public ChestLockEntity load(Location loc) {
                return loadChestLock(loc);
            }
        });
    }

    public boolean chestLockExists(Location location) {
        return this.chestLockEntities.getIfPresent(location) != null;
    }

    private ChestLockEntity loadChestLock(Location loc) {
        final String sql = "SELECT * FROM %s WHERE worldName=:worldName AND blockX=:blockX " +
                "AND blockY=:blockY AND blockZ=:blockZ";
        final var parameters = new HashMap<String, Object>();
        parameters.put("worldName", loc.getWorld().getName());
        parameters.put("blockX", loc.getBlockX());
        parameters.put("blockY", loc.getBlockY());
        parameters.put("blockZ", loc.getBlockZ());
        return this.databaseManager.queryResult(ChestLockEntity.class,
                String.format(sql, TABLE_NAME_CHEST_LOCKS), parameters);
    }


}
