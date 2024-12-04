package de.buuddyyy.buddysystem.commands;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    private final BuddySystemPlugin plugin;

    public PingCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        p.sendMessage(this.plugin.getPrefix() + "§7Dein Ping: §e" + p.getPing() + "ms§7.");
        return true;
    }

}
