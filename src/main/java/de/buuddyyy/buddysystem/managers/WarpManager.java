package de.buuddyyy.buddysystem.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import de.buuddyyy.buddysystem.sql.DatabaseManager;
import de.buuddyyy.buddysystem.sql.entities.WarpEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WarpManager {

    public static final String TABLE_NAME = "warps";

    private final LoadingCache<String, WarpEntity> warps;
    private final DatabaseManager databaseManager;

    public WarpManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.warps = CacheBuilder.newBuilder().build(new CacheLoader<>() {
            @Override
            public WarpEntity load(String warpName) throws Exception {
                return loadWarp(warpName);
            }
        });
    }

    public void createWarp(WarpEntity warpEntity) {
        this.databaseManager.insertEntity(warpEntity);
        this.warps.refresh(warpEntity.getName());
    }

    public void updateWarp(WarpEntity warpEntity) {
        this.databaseManager.updateEntity(warpEntity);
        this.warps.refresh(warpEntity.getName());
    }

    public void deleteWarp(WarpEntity warpEntity) {
        this.databaseManager.deleteEntity(warpEntity);
        this.warps.refresh(warpEntity.getName());
    }

    public WarpEntity getWarp(String warpName) {
        return this.warps.getIfPresent(warpName);
    }

    public void loadWarps() {
        final var warpsList = getWarpEntities();
        warpsList.forEach(we -> {
            try {
                warps.get(we.getName());
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<String> getWarpNames() {
        final var warpNames = new ArrayList<String>();
        final var warpLists = getWarpEntities();
        warpLists.forEach(we -> warpNames.add(we.getName()));
        return warpNames;
    }

    private ArrayList<WarpEntity> getWarpEntities() {
        final var warpsList = new ArrayList<WarpEntity>();
        final String sql = "SELECT * FROM %s";
        warpsList.addAll(databaseManager.queryResults(WarpEntity.class,
                String.format(sql, TABLE_NAME), new HashMap<>()));
        return warpsList;
    }

    private WarpEntity loadWarp(String warpName) {
        final String sql = "SELECT * FROM %s WHERE name=:name";
        final Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("name", warpName);
        return databaseManager.queryResult(WarpEntity.class,
                String.format(sql, TABLE_NAME), parameters);
    }

}
