package de.buuddyyy.buddysystem.commands.homes;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.HomeHandler;
import de.buuddyyy.buddysystem.handlers.TeleportHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelHomeCommand implements CommandExecutor, TabCompleter {

    private final BuddySystemPlugin plugin;
    private final HomeHandler homeHandler;

    public DelHomeCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.homeHandler = plugin.getHomeHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        if (args.length == 0) {
            p.sendMessage(this.plugin.getPrefix() + "§cBitte verwende: §e/delhome <Name>");
            return true;
        }

        String homeName = args[0].toLowerCase();
        final var status = homeHandler.deleteHome(p, homeName);

        String message = String.format((switch (status) {
            case OK -> "Home §e\"%s\" §7wurde gelöscht.";
            case OK_OVERWRITTEN -> null;
            case NOT_EXISTS -> "§cDu hast keinen Home mit dem Namen §e\"%s\"§c!";
            case UNKNOWN_ERROR -> "§4Es gab einen Fehler. Bitte rejoinen.";
        }), homeName);

        if (status == HomeHandler.EnumPlayerHomeStatus.UNKNOWN_ERROR) {
            p.kickPlayer(this.plugin.getPrefix() + message);
        } else {
            p.sendMessage(this.plugin.getPrefix() + message);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            return new ArrayList<>();
        }
        return homeHandler.getHomeNames(p);
    }

}
