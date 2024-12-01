package de.buuddyyy.buddysystem;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.buuddyyy.buddysystem.commands.*;
import de.buuddyyy.buddysystem.commands.homes.DelHomeCommand;
import de.buuddyyy.buddysystem.commands.homes.HomeCommand;
import de.buuddyyy.buddysystem.commands.homes.HomesCommand;
import de.buuddyyy.buddysystem.commands.homes.SetHomeCommand;
import de.buuddyyy.buddysystem.commands.spawn.SetSpawnCommand;
import de.buuddyyy.buddysystem.commands.spawn.SpawnCommand;
import de.buuddyyy.buddysystem.commands.tpa.TpaCommand;
import de.buuddyyy.buddysystem.commands.tpa.TpacceptCommand;
import de.buuddyyy.buddysystem.commands.tpa.TpdenyCommand;
import de.buuddyyy.buddysystem.commands.warps.DelWarpCommand;
import de.buuddyyy.buddysystem.commands.warps.SetWarpCommand;
import de.buuddyyy.buddysystem.commands.warps.WarpCommand;
import de.buuddyyy.buddysystem.configs.MainConfig;
import de.buuddyyy.buddysystem.events.*;
import de.buuddyyy.buddysystem.handlers.*;
import de.buuddyyy.buddysystem.protocol.ActionBarProtocolAdapter;
import de.buuddyyy.buddysystem.sql.DatabaseManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class BuddySystemPlugin extends JavaPlugin {

    @Getter
    private static BuddySystemPlugin plugin;

    @Setter
    private String prefix;

    private MainConfig mainConfig;
    private DatabaseManager databaseManager;
    private PlayerHandler playerHandler;
    private TeleportHandler teleportHandler;
    private HomeHandler homeHandler;
    private SkipNightHandler skipNightHandler;
    private SpawnHandler spawnHandler;
    private WarpHandler warpHandler;
    private EnderChestHandler enderChestHandler;

    private ProtocolManager protocolManager;

    @Override
    public void onLoad() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        BuddySystemPlugin.plugin = this;

        this.mainConfig = new MainConfig(this);
        this.mainConfig.loadConfig();

        this.databaseManager = new DatabaseManager(this.mainConfig);
        this.databaseManager.openConnection();

        initHandlers();

        registerEvents();

        this.protocolManager.addPacketListener(new ActionBarProtocolAdapter(this));

        registerCommands();
    }

    @Override
    public void onDisable() {
        try {
            this.databaseManager.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initHandlers() {
        this.playerHandler = new PlayerHandler(this);
        this.teleportHandler = new TeleportHandler(this);
        this.homeHandler = new HomeHandler(this);
        this.skipNightHandler = new SkipNightHandler(this);
        this.spawnHandler = new SpawnHandler(this);
        this.warpHandler = new WarpHandler(this);
        this.enderChestHandler = new EnderChestHandler(this);
    }

    private void registerEvents() {
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new FoodLevelChangeListener(), this);
        pm.registerEvents(new InventoryCloseListener(this), this);
        pm.registerEvents(new InventoryOpenListener(this), this);
        pm.registerEvents(new PlayerBedEnterListener(this), this);
        pm.registerEvents(new PlayerBedLeaveListener(this), this);
        pm.registerEvents(new PlayerChangedWorldListener(this), this);
        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new PlayerInteractListener(this), this);
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerMoveListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
    }

    private void registerCommands() {
        this.registerCommand("delhome", new DelHomeCommand(this));
        this.getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        this.registerCommand("home", new HomeCommand(this));
        this.getCommand("homes").setExecutor(new HomesCommand(this));
        this.registerCommand("sethome", new SetHomeCommand(this));
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("tpaccept").setExecutor(new TpacceptCommand(this));
        this.getCommand("tpa").setExecutor(new TpaCommand(this));
        this.getCommand("tpdeny").setExecutor(new TpdenyCommand(this));
        this.getCommand("invsee").setExecutor(new InvseeCommand(this));
        this.registerCommand("warp", new WarpCommand(this));
        this.registerCommand("setwarp", new SetWarpCommand(this));
        this.registerCommand("delwarp", new DelWarpCommand(this));
        this.getCommand("ping").setExecutor(new PingCommand(this));
    }

    private void registerCommand(String command, CommandExecutor commandExecutor) {
        final PluginCommand pc = this.getCommand(command);
        pc.setExecutor(commandExecutor);
        if (commandExecutor instanceof TabCompleter) {
            pc.setTabCompleter((TabCompleter) commandExecutor);
        }
    }

}
