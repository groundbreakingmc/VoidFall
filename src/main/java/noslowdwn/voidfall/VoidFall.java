package noslowdwn.voidfall;

import lombok.Getter;
import noslowdwn.voidfall.handlers.Actions;
import noslowdwn.voidfall.handlers.PlayerEvents;
import noslowdwn.voidfall.handlers.Region;
import noslowdwn.voidfall.handlers.YCords;
import noslowdwn.voidfall.utils.UpdateChecker;
import noslowdwn.voidfall.utils.colorizer.IColorizer;
import noslowdwn.voidfall.utils.colorizer.LegacyColorizer;
import noslowdwn.voidfall.utils.colorizer.VanillaColorizer;
import noslowdwn.voidfall.utils.config.ConfigValues;
import noslowdwn.voidfall.utils.logging.BukkitLogger;
import noslowdwn.voidfall.utils.logging.ILogger;
import noslowdwn.voidfall.utils.logging.PaperLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class VoidFall extends JavaPlugin {

    private ConfigValues configValues;
    private IColorizer colorizer;
    private ILogger myLogger;
    private Actions actionsExecutor;

    @Override
    public void onEnable() {
        this.configValues = new ConfigValues(this);
        this.configValues.setupValues();

        final int subVersion = this.getSubVersion();
        this.colorizer = this.getColorizerByVersion(subVersion);
        this.myLogger = this.getLoggerByVersion(subVersion);

        this.actionsExecutor = new Actions(this);

        this.registerCommand();

        this.getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new YCords(this), this);

        final boolean isWgEventsEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuardEvents");
        if (isWgEventsEnabled) {
            this.getServer().getPluginManager().registerEvents(new Region(this), this);
        } else {
            this.myLogger.info("[VoidFall] Actions on region enter/leave will be disabled!");
            this.myLogger.info("[VoidFall] Please download WorldGuardEvents to enable them.");
            this.myLogger.info("[VoidFall] https://www.spigotmc.org/resources/worldguard-events.65176/");
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> new UpdateChecker(this).checkVersion(), 60L);
    }

    private void registerCommand() {
        final VoidFallCommand command = new VoidFallCommand(this);
        super.getCommand("voidfall").setExecutor(command);
        super.getCommand("voidfall").setTabCompleter(command);
    }

    public IColorizer getColorizerByVersion(final int subVersion) {
        final boolean is16OrAbove = subVersion >= 16;
        return is16OrAbove ? new LegacyColorizer() : new VanillaColorizer();
    }

    public ILogger getLoggerByVersion(final int subVersion) {
        final boolean is19OrAbove = subVersion >= 19;
        return is19OrAbove ? new PaperLogger(this) : new BukkitLogger(this);
    }

    public int getSubVersion() {
        try {
            return Integer.parseInt(super.getServer().getVersion().split("\\.")[1]);
        } catch (final NumberFormatException ex) {
            super.getLogger().warning("Failed to extract server version. Plugin may not work correctly!");
            return 0;
        }
    }
}