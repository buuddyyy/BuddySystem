package de.buuddyyy.buddysystem.events;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final BuddySystemPlugin plugin;

    public PlayerDeathListener(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final var p = event.getEntity();
        p.spigot().sendMessage(this.createDeathMessageForPlayer(p.getLocation()));
    }

    private TextComponent createDeathMessageForPlayer(Location deathLocation) {
        var tc = new TextComponent(this.plugin.getPrefix() + "§cDu bist gestorben. ");
        var cordTc = new TextComponent("§8[§eKoordinaten§8]");
        cordTc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                new TextComponent("§7Welt: §e" + deathLocation.getWorld().getName() + "\n"),
                new TextComponent("§7X: §e" + deathLocation.getBlockX() + "\n"),
                new TextComponent("§7Y: §e" + deathLocation.getBlockY() + "\n"),
                new TextComponent("§7Z: §e" + deathLocation.getBlockZ())
        }));
        tc.addExtra(cordTc);
        return tc;
    }


}
