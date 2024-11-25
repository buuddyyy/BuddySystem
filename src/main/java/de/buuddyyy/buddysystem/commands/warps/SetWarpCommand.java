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

public class SetWarpCommand implements CommandExecutor, TabCompleter {

    private final BuddySystemPlugin plugin;
    private final WarpHandler warpHandler;

    public SetWarpCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.warpHandler = this.plugin.getWarpHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }

        if (args.length != 1) {
            p.sendMessage(this.plugin.getPrefix() + "§cBitte verwende: §e/setwarp <Name>");
            return true;
        }

        final var warpName = args[0].toLowerCase();
        final var status = this.warpHandler.setWarp(p, warpName, p.getLocation());

        String message = String.format((switch (status) {
            case OK -> "Warp §e\"%s\" §7wurde gesetzt.";
            case OK_OVERWRITTEN -> "Warp §e\"%s\" §7wurde überschrieben.";
            case NOT_EXISTS -> null;
            case NOT_OWNER -> "§cDu darfst diesen Warp nicht überschreiben.";
            case UNKNOWN_ERROR -> "§4Es gab einen Fehler. Bitte rejoine.";
        }), warpName);

        if (status == WarpHandler.EnumPlayerWarpStatus.UNKNOWN_ERROR) {
            p.kickPlayer(this.plugin.getPrefix() + message);
        } else {
            p.sendMessage(this.plugin.getPrefix() + message);
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
