package de.buuddyyy.buddysystem.commands.warps;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {

    private final BuddySystemPlugin plugin;
    private final WarpHandler warpHandler;

    public WarpCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.warpHandler = plugin.getWarpHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }

        if (args.length != 1) {
            p.sendMessage(this.plugin.getPrefix() + "§cBitte verwende: §e/warp <Name>");
            return true;
        }

        final var warpName = args[0].toLowerCase();
        final var status = warpHandler.teleportPlayerToWarp(p, warpName);

        if (status == WarpHandler.EnumPlayerWarpStatus.NOT_EXISTS) {
            p.sendMessage(this.plugin.getPrefix() + "§cEs gibt kein Warp mit dem Namen!");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return new ArrayList<>();
        }
        return warpHandler.getWarpNames();
    }

}
