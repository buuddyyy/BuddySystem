package de.buuddyyy.buddysystem.commands;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EnderChestCommand implements CommandExecutor {

    private final BuddySystemPlugin plugin;

    public EnderChestCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        if (p.isOp() && args.length == 1) {
            Player targetPlayer;
            if ((targetPlayer = Bukkit.getPlayer(args[0])) == null) {
                p.sendMessage(this.plugin.getPrefix() + "§cDieser Spieler ist nicht online!");
                return true;
            }
            if (p.equals(targetPlayer)) {
                p.sendMessage(this.plugin.getPrefix() + "§cDu kannst nicht mit dir selbst interagieren!");
                return true;
            }
            p.sendMessage(this.plugin.getPrefix() + String.format("Du schaust dir die Enderchest von §e%s §7an.",
                    targetPlayer.getName()));
            p.openInventory(targetPlayer.getEnderChest());
        } else {
            p.openInventory(p.getEnderChest());
        }
        return true;
    }

}
