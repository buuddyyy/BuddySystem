package de.buuddyyy.buddysystem.commands.tpa;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.TeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {

    private final BuddySystemPlugin plugin;
    private final TeleportHandler teleportHandler;

    public TpaCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.teleportHandler = plugin.getTeleportHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        if (args.length != 1) {
            p.sendMessage(this.plugin.getPrefix() + "§cBitte verwende: §e/tpa <Spieler>");
            return true;
        }
        final String playerName = args[0];
        Player toPlayer;
        if ((toPlayer = Bukkit.getPlayer(playerName)) == null) {
            p.sendMessage(this.plugin.getPrefix() + "§cDieser Spieler ist nicht online!");
            return true;
        }
        if (p.equals(toPlayer)) {
            p.sendMessage(this.plugin.getPrefix() + "§cDu kannst nicht mit dir selbst interagieren!");
            return true;
        }
        teleportHandler.sendTeleportRequest(p, toPlayer);
        return true;
    }

}
