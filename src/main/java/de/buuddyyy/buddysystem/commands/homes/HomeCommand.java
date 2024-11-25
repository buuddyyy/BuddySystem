package de.buuddyyy.buddysystem.commands.homes;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.HomeHandler;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HomeCommand implements CommandExecutor, TabExecutor {

    private final BuddySystemPlugin plugin;
    private final HomeHandler homeHandler;

    public HomeCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.homeHandler = plugin.getHomeHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        String homeName = ((args.length == 0) ? "home" : args[0]).toLowerCase();
        final var status = homeHandler.teleportPlayerToHome(p, homeName);

        if (status == HomeHandler.EnumPlayerHomeStatus.NOT_EXISTS) {
            p.sendMessage(this.plugin.getPrefix() + "§cDu hast kein Home mit dem Namen!");
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
