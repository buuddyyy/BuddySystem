package de.buuddyyy.buddysystem.commands;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand implements CommandExecutor {

    private final BuddySystemPlugin plugin;

    public InvseeCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return true;
        }
        if (!p.isOp()) {
            p.sendMessage(this.plugin.getPrefix() + "§cDu hast keine Rechte für diesen Befehl!");
            return true;
        }
        if (args.length == 0) {
            p.sendMessage(this.plugin.getPrefix() + "§cBitte verwende: §e/invsee <Spieler>");
            return true;
        }
        Player targetPlayer;
        if ((targetPlayer = Bukkit.getPlayer(args[0])) == null) {
            p.sendMessage(this.plugin.getPrefix() + "§cDieser Spieler ist nicht online!");
            return true;
        }
        if (p.equals(targetPlayer)) {
            p.sendMessage(this.plugin.getPrefix() + "§cDu kannst nicht mit dir selbst interagieren!");
            return true;
        }
        p.sendMessage(this.plugin.getPrefix() + String.format("Du schaust dir das Inventar von §e%s §7an.",
                targetPlayer.getName()));
        p.openInventory(targetPlayer.getInventory());
        return true;
    }

}
