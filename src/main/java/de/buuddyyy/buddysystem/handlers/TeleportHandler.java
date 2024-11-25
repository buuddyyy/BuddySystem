package de.buuddyyy.buddysystem.handlers;

import com.google.common.collect.Maps;
import de.buuddyyy.buddysystem.BuddySystemPlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TeleportHandler {

    private static final Map<UUID, BukkitTask> PLAYER_TELEPORT_RUNNABLES = Maps.newHashMap();
    private static final Map<UUID, List<PlayerTeleportRequest>> PLAYER_TELEPORT_REQUESTS = Maps.newHashMap();

    private final BuddySystemPlugin plugin;

    private static final int TELEPORT_DELAY_SECONDS = 3;

    public TeleportHandler(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    public EnumPlayerTeleportStatus handleMove(PlayerMoveEvent event) {
        final Player p = event.getPlayer();
        if (!PLAYER_TELEPORT_RUNNABLES.containsKey(p.getUniqueId()) || event.getTo() == null) {
            return EnumPlayerTeleportStatus.OK;
        }

        final Location fromLoc = event.getFrom();
        final Location toLoc = event.getTo();

        if (fromLoc.getX() == toLoc.getX()
                && fromLoc.getBlockY() == toLoc.getBlockY()
                && fromLoc.getZ() == toLoc.getZ()) {
            return EnumPlayerTeleportStatus.OK;
        }

        cancelAndRemoveTeleportTask(p);
        return EnumPlayerTeleportStatus.ABORTED_MOVED;
    }

    public static void teleportPlayer(Player targetPlayer, Location teleportLocation) {
        if (PLAYER_TELEPORT_RUNNABLES.containsKey(targetPlayer.getUniqueId())) {
            return;
        }

        PLAYER_TELEPORT_RUNNABLES.put(targetPlayer.getUniqueId(), Bukkit.getScheduler()
                .runTaskLater(BuddySystemPlugin.getPlugin(), () -> {
            targetPlayer.teleport(teleportLocation);

            cancelAndRemoveTeleportTask(targetPlayer);
        }, 20L * TELEPORT_DELAY_SECONDS));

        targetPlayer.sendMessage(BuddySystemPlugin.getPlugin().getPrefix() + "Du wirst §7teleportiert. " +
                "Du darfst dich §e" + TELEPORT_DELAY_SECONDS + " Sekunden §7lang nicht bewegen.");
    }

    private static void cancelAndRemoveTeleportTask(Player p) {
        PLAYER_TELEPORT_RUNNABLES.get(p.getUniqueId()).cancel();
        PLAYER_TELEPORT_RUNNABLES.remove(p.getUniqueId());
    }

    public void teleportToPlayer(Player toPlayer, Player fromPlayer) {
        if (PLAYER_TELEPORT_RUNNABLES.containsKey(toPlayer.getUniqueId())) {
            return;
        }
        final List<PlayerTeleportRequest> requestLists = PLAYER_TELEPORT_REQUESTS.get(toPlayer.getUniqueId());
        if (!hasSentTeleportRequest(requestLists, fromPlayer)) {
            toPlayer.sendMessage(this.plugin.getPrefix() + "§cDu hast keine Teleportanfrage von §e"
                    + fromPlayer.getName() + " §cerhalten!");
            return;
        }
        this.deleteTeleportRequest(fromPlayer, toPlayer);
        PLAYER_TELEPORT_REQUESTS.put(toPlayer.getUniqueId(), requestLists);

        toPlayer.sendMessage(this.plugin.getPrefix() + "§aDu hast die Teleportanfrage von §e"
                + fromPlayer.getName() + " §aangenommen.");
        fromPlayer.sendMessage(this.plugin.getPrefix() + "§e" + toPlayer.getName() + " §ahat deine "
                + "Teleportanfrage angenommen.");

        teleportPlayer(fromPlayer, toPlayer.getLocation());
    }

    public void sendTeleportRequest(Player fromPlayer, Player toPlayer) {
        final List<PlayerTeleportRequest> requestLists = PLAYER_TELEPORT_REQUESTS.getOrDefault(toPlayer.getUniqueId(),
                new ArrayList<>());
        if (hasSentTeleportRequest(requestLists, fromPlayer)) {
            fromPlayer.sendMessage(this.plugin.getPrefix() + "§cDu hast bereits eine Teleportanfrage verschickt!");
            return;
        }
        requestLists.add(new PlayerTeleportRequest(fromPlayer, toPlayer));
        PLAYER_TELEPORT_REQUESTS.put(toPlayer.getUniqueId(), requestLists);
        TextComponent acceptRequestCommand = new TextComponent("§8[§aAnnehmen§8]");
        acceptRequestCommand.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/tpaccept " + fromPlayer.getName()));
        acceptRequestCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                new TextComponent("§7Teleportanfrage von §e" + fromPlayer.getName() + " §7annehmen")
        }));
        TextComponent denyRequestCommand = new TextComponent("§8[§cAblehnen§8]");
        denyRequestCommand.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/tpdeny " + fromPlayer.getName()));
        denyRequestCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                new TextComponent("§7Teleportanfrage von §e" + fromPlayer.getName() + " §7ablehnen")
        }));
        toPlayer.spigot().sendMessage(new TextComponent(this.plugin.getPrefix() + "§e" + fromPlayer.getName()
                + " §7hat dir eine §eTeleportanfrage §7geschickt: "), acceptRequestCommand,
                new TextComponent(" "), denyRequestCommand);
        fromPlayer.sendMessage(this.plugin.getPrefix() + "Du hast eine Teleportanfrage an §e"
                + toPlayer.getName() + " §7verschickt.");

    }

    public boolean hasSentTeleportRequest(Player fromPlayer, Player toPlayer) {
        final List<PlayerTeleportRequest> requestLists = PLAYER_TELEPORT_REQUESTS.getOrDefault(toPlayer.getUniqueId(),
                new ArrayList<>());
        return hasSentTeleportRequest(requestLists, fromPlayer);
    }

    private boolean hasSentTeleportRequest(List<PlayerTeleportRequest> playerTeleportRequests, Player fromPlayer) {
        if (playerTeleportRequests.isEmpty())
            return false;
        for (PlayerTeleportRequest request : playerTeleportRequests) {
            if (fromPlayer.getUniqueId().equals(request.fromPlayer.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void deleteTeleportRequest(Player fromPlayer, Player toPlayer) {
        final List<PlayerTeleportRequest> requestLists = PLAYER_TELEPORT_REQUESTS.getOrDefault(toPlayer.getUniqueId(),
                new ArrayList<>());
        if (!hasSentTeleportRequest(requestLists, fromPlayer)) {
            return;
        }
        final var optional = requestLists.stream().filter(obj -> obj.fromPlayer.getUniqueId()
                .equals(fromPlayer.getUniqueId())).findFirst();
        if (optional.isEmpty()) {
            return;
        }
        var ptr = optional.get();
        ptr.cancelTask();
        requestLists.remove(ptr);
        PLAYER_TELEPORT_REQUESTS.put(toPlayer.getUniqueId(), requestLists);
    }

    private class PlayerTeleportRequest implements Runnable {

        private final Player fromPlayer;
        private final Player toPlayer;
        private final String fromPlayerName;
        private final String toPlayerName;
        private BukkitTask task;

        public PlayerTeleportRequest(Player fromPlayer, Player toPlayer) {
            this.fromPlayer = fromPlayer;
            this.toPlayer = toPlayer;
            this.fromPlayerName = fromPlayer.getName();
            this.toPlayerName = toPlayer.getName();
            this.task = Bukkit.getScheduler().runTaskLater(plugin, this, 20L*60*3);
        }

        public void cancelTask() {
            if (this.task == null)
                return;
            this.task.cancel();
            this.task = null;
        }

        @Override
        public void run() {
            if (this.task == null)
                return;
            this.task = null;

            if (!hasSentTeleportRequest(fromPlayer, toPlayer))
                return;

            deleteTeleportRequest(fromPlayer, toPlayer);

            if (fromPlayer != null && fromPlayer.isOnline()) {
                fromPlayer.sendMessage(String.format("%s§7Deine Teleportanfrage an §e%s §7ist abgelaufen.",
                        plugin.getPrefix(), toPlayerName));
            }

            if (toPlayer != null && toPlayer.isOnline()) {
                toPlayer.sendMessage(String.format("%s§7Die Teleportanfrage von §e%s §7ist abgelaufen.",
                        plugin.getPrefix(), fromPlayerName));
            }
        }

    }

    public enum EnumPlayerTeleportStatus {
        OK,
        ALREADY_SENT,
        ABORTED_MOVED
    }

}
