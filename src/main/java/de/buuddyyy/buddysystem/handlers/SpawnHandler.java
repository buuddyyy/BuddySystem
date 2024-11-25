package de.buuddyyy.buddysystem.handlers;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.configs.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnHandler {

    public static Location spawnLocation;
    private final BuddySystemPlugin plugin;

    public SpawnHandler(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        SpawnHandler.spawnLocation = null;
        this.loadFromConfig();
    }

    public void teleportPlayerToSpawn(Player targetPlayer) {
        if (SpawnHandler.spawnLocation == null) {
            return;
        }
        TeleportHandler.teleportPlayer(targetPlayer, SpawnHandler.spawnLocation);
    }

    public void teleportPlayerToSpawnImmediately(Player targetPlayer) {
        if (SpawnHandler.spawnLocation == null) {
            return;
        }
        targetPlayer.teleport(SpawnHandler.spawnLocation);
    }

    public void setSpawnLocation(Location location) {
        SpawnHandler.spawnLocation = location;
        final MainConfig config = this.plugin.getMainConfig();
        final FileConfiguration fc = config.getConfiguration();
        fc.set("spawn.worldName", location.getWorld().getName());
        fc.set("spawn.x", location.getX());
        fc.set("spawn.y", location.getY());
        fc.set("spawn.z", location.getZ());
        fc.set("spawn.yaw", location.getYaw());
        fc.set("spawn.pitch", location.getPitch());
        config.saveConfig();
    }

    private void loadFromConfig() {
        final MainConfig config = this.plugin.getMainConfig();
        final FileConfiguration fc = config.getConfiguration();
        if (fc.contains("spawn")) {
            String worldName = fc.getString("spawn.worldName");
            World w = Bukkit.getWorld(worldName);
            double x = fc.getDouble("spawn.x");
            double y = fc.getDouble("spawn.y");
            double z = fc.getDouble("spawn.z");
            float yaw = (float) fc.getDouble("spawn.yaw");
            float pitch = (float) fc.getDouble("spawn.pitch");
            final Location loc = new Location(w, x, y, z);
            loc.setYaw(yaw);
            loc.setPitch(pitch);
            SpawnHandler.spawnLocation = loc;
        }
    }

}
