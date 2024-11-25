package de.buuddyyy.buddysystem.handlers;

import com.google.common.collect.Maps;
import de.buuddyyy.buddysystem.BuddySystemPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkipNightHandler {

    private static final Map<World, List<Player>> PLAYERS_SLEEPING = Maps.newHashMap();
    private static final Map<World, BukkitTask> WORLD_SLEEPING_RUNNABLE = Maps.newHashMap();

    private final BuddySystemPlugin plugin;

    public SkipNightHandler(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    public void handleBedEnter(Player player) {
        final World w = player.getWorld();
        if (!isNight(w))
            return;
        int totalPlayers = w.getPlayers().size();
        if (totalPlayers == 1)
            return;
        final List<Player> sleepingPlayers = PLAYERS_SLEEPING.getOrDefault(w, new ArrayList<>());
        if (!sleepingPlayers.contains(player)) {
            sleepingPlayers.add(player);
            PLAYERS_SLEEPING.put(w, sleepingPlayers);
            int sleepingCount = sleepingPlayers.size();
            int requiredPlayers = (int) Math.round(totalPlayers/2.0);
            w.getPlayers().forEach(pAll -> pAll.sendMessage(
                    String.format("§e%s §7schläft... zZzZ §8[§e%d§7/%d§8]", player.getName(),
                            sleepingCount, requiredPlayers)));
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> checkSleepingPlayers(w), 5L);
    }

    public void handleBedLeave(Player player) {
        final World w = player.getWorld();
        if (!PLAYERS_SLEEPING.containsKey(w))
            return;
        if (!isNight(w))
            return;
        int totalPlayers = w.getPlayers().size();
        if (totalPlayers == 1)
            return;
        final List<Player> sleepingPlayers = PLAYERS_SLEEPING.get(w);
        if (sleepingPlayers.contains(player)) {
            sleepingPlayers.remove(player);
            PLAYERS_SLEEPING.put(w, sleepingPlayers);
            int sleepingCount = sleepingPlayers.size();
            int requiredPlayers = (int) Math.round(totalPlayers/2.0);
            w.getPlayers().forEach(pAll -> pAll.sendMessage(
                    String.format("§e%s §7schläft nicht mehr §8[§e%d§7/%d§8]", player.getName(),
                            sleepingCount, requiredPlayers)));
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> checkSleepingPlayers(w), 5L);
    }

    public void handlePlayerChangeWorld(Player player, World fromWorld) {
        if (!PLAYERS_SLEEPING.containsKey(fromWorld)) {
            return;
        }
        final List<Player> sleepingPlayers = PLAYERS_SLEEPING.get(fromWorld);
        if (sleepingPlayers.contains(player)) {
            sleepingPlayers.remove(player);
            PLAYERS_SLEEPING.put(fromWorld, sleepingPlayers);
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> checkSleepingPlayers(fromWorld), 20L);
    }

    public void handlePlayerQuit(Player player) {
        final World w = player.getWorld();
        if (!PLAYERS_SLEEPING.containsKey(w))
            return;
        final List<Player> sleepingPlayers = PLAYERS_SLEEPING.get(w);
        if (!sleepingPlayers.contains(player))
            return;
        sleepingPlayers.remove(player);
        PLAYERS_SLEEPING.put(w, sleepingPlayers);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> checkSleepingPlayers(w), 5L);
    }

    private void checkSleepingPlayers(World world) {
        int totalPlayers = world.getPlayers().size();
        int sleepingPlayers = PLAYERS_SLEEPING.getOrDefault(world, new ArrayList<>()).size();
        if (totalPlayers > 0 && sleepingPlayers >= Math.round((float) totalPlayers /2)) {
            if (!WORLD_SLEEPING_RUNNABLE.containsKey(world)) {
                BukkitTask task = Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    world.setTime(0);
                    world.setStorm(false);
                    world.setThundering(false);
                    PLAYERS_SLEEPING.get(world).clear();
                    cancelAndRemoveTask(world);
                }, 20L * 5);
                WORLD_SLEEPING_RUNNABLE.put(world, task);
            }
            world.getPlayers().forEach(pAll -> pAll.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent("§aDie Nacht wird übersprungen...")));
            return;
        }
        if (!WORLD_SLEEPING_RUNNABLE.containsKey(world))
            return;
        cancelAndRemoveTask(world);
    }

    private static void cancelAndRemoveTask(World world) {
        WORLD_SLEEPING_RUNNABLE.get(world).cancel();
        WORLD_SLEEPING_RUNNABLE.remove(world);
    }

    private boolean isNight(World world) {
        long time = world.getTime();
        return time >= 12000 && time <= 24000;
    }

}
