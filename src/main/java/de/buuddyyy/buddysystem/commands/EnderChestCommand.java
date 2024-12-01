package de.buuddyyy.buddysystem.commands;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.EnderChestHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChestCommand implements CommandExecutor {

    private final BuddySystemPlugin plugin;
    private final EnderChestHandler enderChestHandler;

    public EnderChestCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.enderChestHandler = plugin.getEnderChestHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        /*
        if (p.isOp() && args.length == 1) {
            final var targetPlayerName = args[0];
            if (p.getName().equalsIgnoreCase(targetPlayerName)) {
                p.sendMessage(this.plugin.getPrefix() + "§cDu kannst nicht mit dir selbst interagieren!");
                return true;
            }

            var status = this.enderChestHandler.openEnderChestFromOtherPlayer(p, targetPlayerName);

            String message = String.format(switch(status) {
                case OK -> "§7Du schaust dir die Ender Chest von §e%s §7an.";
                case PLAYER_NOT_EXISTS -> "§cDieser Spieler existiert nicht!";
                case PLAYER_HAS_NOT_ENDERCHEST -> "§cDieser Spieler hat noch keine EnderChest!";
            }, this.getPlayerName(targetPlayerName));

            p.sendMessage(this.plugin.getPrefix() + message);
        } else {
        */
            this.enderChestHandler.openEnderChest(p);
        //}
        return true;
    }

    private String getPlayerName(String playerName) {
        final var ph = this.plugin.getPlayerHandler();
        var pe = ph.getPlayerEntityByName(playerName);
        return (pe != null) ? pe.getPlayerName() : "UNKNOWN";
    }

}
