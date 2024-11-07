package groundbreakingmc.voidfall.utils.config;

import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class ConfigLoader {

    private final VoidFall plugin;

    public ConfigLoader(final VoidFall plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration loadAndGet(final String fileName, final double fileVersion) {
        final File file = new File(this.plugin.getDataFolder(), fileName + ".yml");
        if (!file.exists()) {
            this.plugin.saveResource(fileName + ".yml", false);
        }

        try {
            new YamlConfiguration().load(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.setDefaults(config, fileName);
        config = this.checkVersion(config, fileName, fileVersion);

        return config;
    }

    private void setDefaults(final FileConfiguration config, final String fileName) {
        try (final InputStream defConfigStream = this.plugin.getResource(fileName + ".yml")) {
            if (defConfigStream != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
            }
        } catch (IOException e) {
            this.plugin.getMyLogger().warning("Error loading default configuration: " + e.getMessage());
        }
    }

    private FileConfiguration checkVersion(final FileConfiguration config, final String fileName, final double fileVersion) {
        final double configVersion = config.getDouble("settings.config-version", 0);

        if (configVersion != fileVersion) {
            this.createBackupAndUpdate(fileName);
            return this.loadAndGet(fileName, fileVersion);
        }

        return config;
    }

    private void createBackupAndUpdate(final String fileName) {
        final File folder = this.plugin.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            this.plugin.getMyLogger().warning("An error occurred while creating the backups folder!");
            return;
        }

        final File file = new File(folder, fileName + ".yml");
        final int backupNumber = folder.listFiles().length;
        final File backupFile = new File(folder, fileName + "_backup_" + backupNumber + ".yml");

        if (file.renameTo(backupFile)) {
            this.plugin.saveResource(fileName + ".yml", true);
        } else {
            this.plugin.getMyLogger().warning("Your configuration file \"" + fileName + ".yml\" is outdated, but creating a new one isn't possible.");
        }
    }
}