package de.buuddyyy.buddysystem.commands.spawn;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.SpawnHandler;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
public class SpawnCommand implements CommandExecutor {

    private final BuddySystemPlugin plugin;
    private final SpawnHandler spawnHandler;

    public SpawnCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.spawnHandler = plugin.getSpawnHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        if (SpawnHandler.spawnLocation == null) {
            p.sendMessage(this.plugin.getPrefix() + "§eSpawn-Location §cwurde noch nicht gesetzt!");
            return true;
        }
        spawnHandler.teleportPlayerToSpawn(p);
        return true;
    }

}
