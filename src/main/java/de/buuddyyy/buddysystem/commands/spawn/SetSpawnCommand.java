package de.buuddyyy.buddysystem.commands.spawn;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import de.buuddyyy.buddysystem.handlers.SpawnHandler;
import de.buuddyyy.buddysystem.handlers.TeleportHandler;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
public final class SetSpawnCommand implements CommandExecutor {

    private final BuddySystemPlugin plugin;
    private final SpawnHandler spawnHandler;

    public SetSpawnCommand(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        this.spawnHandler = plugin.getSpawnHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        if (!p.isOp()) {
            p.sendMessage(this.plugin.getPrefix() + "§cDu hast keine Rechte für diesen Befehl!");
            return true;
        }
        spawnHandler.setSpawnLocation(p.getLocation());
        p.sendMessage(this.plugin.getPrefix() + "§eSpawn-Location §7wurde gesetzt.");
        return true;
    }

}
