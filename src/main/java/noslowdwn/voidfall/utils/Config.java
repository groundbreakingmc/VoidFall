package noslowdwn.voidfall.utils;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    private Config() {
        throw new ExceptionInInitializerError("This class may not be initialized!");
    }

    private static File file;

    private final VoidFall plugin;

    public Config(final VoidFall plugin) {
        this.plugin = plugin;
    }

    public void load() {
        file = new File(this.plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            this.plugin.saveResource("config.yml", false);
        }

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkVersion() {
        double version = 1.3;
        final FileConfiguration config = this.plugin.getConfig();
        if (config.getDouble("config-version") != version || !config.getKeys(false).contains("config-version")) {
            int i = 1;
            File backupFile;
            do {
                backupFile = new File(this.plugin.getDataFolder(), "config_backup_" + i + ".yml");
                i++;
            } while (backupFile.exists());

            if (file.renameTo(backupFile)) {
                Bukkit.getConsoleSender().sendMessage("§cYour configuration file is old and was renamed to: §7" + backupFile.getName());
                load();
            } else {
                Bukkit.getConsoleSender().sendMessage("§cYour configuration file is old, but §ncreate new is not possible§c.");
            }
        }
    }
}