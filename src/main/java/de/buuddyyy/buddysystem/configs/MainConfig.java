package de.buuddyyy.buddysystem.configs;

import com.google.common.collect.Maps;
import de.buuddyyy.buddysystem.BuddySystemPlugin;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public final class MainConfig {

    private final BuddySystemPlugin plugin;
    private final File file;
    @Getter private final FileConfiguration configuration;

    @SneakyThrows
    public MainConfig(BuddySystemPlugin plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();
        this.file = new File(plugin.getDataFolder(), "config.yml");
        if (!this.file.exists())
            this.file.createNewFile();
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        this.createDefaults();
    }

    private void createDefaults() {
        final Map<String, Object> defaultsMap = Maps.newHashMap();
        defaultsMap.put("general.prefix", "&7[&bBuddySystem&7] &7");
        defaultsMap.put("general.tablist.header", Arrays.asList("&7[&bBuddySystem&7]", ""));
        defaultsMap.put("general.tablist.footer", Arrays.asList("", "&7Programmiert von &cbuddy"));
        defaultsMap.put("sql.driverClass", "com.mysql.cj.jdbc.Driver");
        defaultsMap.put("sql.url", "jdbc:mysql://localhost:3306/yourdatabase");
        defaultsMap.put("sql.username", "yourusername");
        defaultsMap.put("sql.password", "yourpassword");
        defaultsMap.put("sql.showSql", false);
        defaultsMap.put("sql.formatSql", false);
        defaultsMap.put("sql.commandSql", "update");
        this.configuration.addDefaults(defaultsMap);
        this.configuration.options().copyDefaults(true);
        this.saveConfig();
    }

    public void loadConfig() {
        this.plugin.setPrefix(ChatColor.translateAlternateColorCodes('&', getString("general.prefix")));
    }

    public String getString(final String key) {
        if (!this.configuration.contains(key))
            throw new IllegalArgumentException("Configuration key " + key + " does not exists.");
        return this.configuration.getString(key);
    }

    public Boolean getBoolean(final String key) {
        if (!this.configuration.contains(key))
            throw new IllegalArgumentException("Configuration key " + key + " does not exists.");
        return this.configuration.getBoolean(key);
    }

    public void saveConfig() {
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
