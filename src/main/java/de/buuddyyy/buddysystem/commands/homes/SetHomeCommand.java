package de.buuddyyy.buddysystem.commands.homes;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.HomeHandler;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SetHomeCommand implements CommandExecutor, TabCompleter {

    private final BuddySystemPlugin plugin;
    private final HomeHandler homeHandler;

    public SetHomeCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.homeHandler = plugin.getHomeHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        String homeName = ((args.length == 0) ? "home" : args[0]).toLowerCase();
        final var status = homeHandler.setPlayerHome(p, homeName, p.getLocation());

        final String message = String.format((switch (status) {
            case OK -> "Home §e\"%s\" §7wurde gesetzt.";
            case OK_OVERWRITTEN -> "Home §e\"%s\" §7wurde überschrieben.";
            case NOT_EXISTS -> null;
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
