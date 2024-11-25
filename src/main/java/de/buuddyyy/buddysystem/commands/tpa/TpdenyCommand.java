package de.buuddyyy.buddysystem.commands.tpa;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.TeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpdenyCommand implements CommandExecutor {

    private final BuddySystemPlugin plugin;
    private final TeleportHandler teleportHandler;

    public TpdenyCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.teleportHandler = plugin.getTeleportHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        if (args.length != 1) {
            p.sendMessage(this.plugin.getPrefix() + "§cBitte verwende: §e/tpdeny <Spieler>");
            return true;
        }
        final String playerName = args[0];
        Player fromPlayer;
        if ((fromPlayer = Bukkit.getPlayer(playerName)) == null) {
            p.sendMessage(this.plugin.getPrefix() + "§cDieser Spieler ist nicht online.");
            return true;
        }
        if (p.equals(fromPlayer)) {
            p.sendMessage(this.plugin.getPrefix() + "§cDu kannst nicht mit dir selbst interagieren.");
            return true;
        }
        if (!this.teleportHandler.hasSentTeleportRequest(fromPlayer, p)) {
            p.sendMessage(this.plugin.getPrefix() + "§cDu hast keine Teleportanfrage von §e"
                    + fromPlayer.getName() + " §cerhalten.");
            return true;
        }
        this.teleportHandler.deleteTeleportRequest(fromPlayer, p);
        p.sendMessage(this.plugin.getPrefix() + "§7Du hast die Teleportanfrage von §e" + fromPlayer.getName()
                + " §cabgelehnt.");
        fromPlayer.sendMessage(this.plugin.getPrefix() + "§e" + p.getName()
                + " §chat die Teleportanfrage abgelehnt!");
        return true;
    }
}
