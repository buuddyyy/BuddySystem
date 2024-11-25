package de.buuddyyy.buddysystem.commands.homes;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.HomeHandler;
import de.buuddyyy.buddysystem.handlers.TeleportHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomesCommand implements CommandExecutor {

    private final BuddySystemPlugin plugin;
    private final HomeHandler homeHandler;

    public HomesCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.homeHandler = plugin.getHomeHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        final var homes = this.homeHandler.getHomeNames(p);
        if (homes.isEmpty()) {
            p.sendMessage(this.plugin.getPrefix() + "§cDu hast keine Homes.");
            return true;
        }
        var text = new TextComponent(this.plugin.getPrefix() + "Das sind deine Homes: ");
        for (int i = 0; i < homes.size(); i++) {
            text.addExtra(createText(homes.get(i), i));
            if (i < homes.size()-1) {
                text.addExtra(new TextComponent("§7, "));
            }
        }
        p.spigot().sendMessage(text);
        return false;
    }

    private TextComponent createText(String homeName, int index) {
        var text = new TextComponent(String.format("%s%s", (index % 2) == 0 ? ChatColor.DARK_AQUA
                : ChatColor.BLUE, homeName));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                String.format("/home %s", homeName)));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                new TextComponent("§aKlick hier, um dich da hin zu teleportieren.")
        }));
        return text;
    }
}
