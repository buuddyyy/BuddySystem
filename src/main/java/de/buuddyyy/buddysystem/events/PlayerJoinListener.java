package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.configs.MainConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.stream.Collectors;

public class PlayerJoinListener implements Listener {

    private final BuddySystemPlugin plugin;

    private String header = "";
    private String footer = "";

    public PlayerJoinListener(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.prepareHeaderAndFooter(plugin.getMainConfig());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        plugin.getPlayerHandler().handlePlayerJoin(p);
        plugin.getEnderChestHandler().handlePlayerJoin(p);

        p.setPlayerListHeaderFooter(header, footer);
    }

    private void prepareHeaderAndFooter(MainConfig config) {
        final var fc = config.getConfiguration();
        if (fc.contains("general.tablist.header") && fc.isList("general.tablist.header")) {
            var headerList = fc.getStringList("general.tablist.header");
            var header = String.join("\n", headerList);
            this.header = ChatColor.translateAlternateColorCodes('&', header);
        }
        if (fc.contains("general.tablist.footer") && fc.isList("general.tablist.footer")) {
            var footerList = fc.getStringList("general.tablist.footer");
            var footer = String.join("\n", footerList);
            this.footer = ChatColor.translateAlternateColorCodes('&', footer);
        }
    }

}
