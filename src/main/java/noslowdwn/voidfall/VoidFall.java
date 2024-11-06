package noslowdwn.voidfall;

import lombok.Getter;
import noslowdwn.voidfall.listeners.*;
import noslowdwn.voidfall.utils.UpdatesChecker;
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

    private JoinListener joinListener;
    private QuitListener quitListener;
    private DeathListener deathListener;
    private Region regionListener;
    private HeightListerner heightListerner;

    @Override
    public void onEnable() {
        this.configValues = new ConfigValues(this);

        this.registerListenerClasses();
        this.configValues.setupValues();

        final int subVersion = this.getSubVersion();
        this.colorizer = this.getColorizerByVersion(subVersion);
        this.myLogger = this.getLoggerByVersion(subVersion);

        this.registerCommand();

        this.registerRegionsListener();

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> new UpdatesChecker(this).check(), 60L);
    }

    private void registerCommand() {
        final VoidFallCommand command = new VoidFallCommand(this);
        super.getCommand("voidfall").setExecutor(command);
        super.getCommand("voidfall").setTabCompleter(command);
    }

    private void registerListenerClasses() {
        this.joinListener = new JoinListener(this);
        this.quitListener = new QuitListener(this);
        this.deathListener = new DeathListener(this);
        this.regionListener = new Region(this);
        this.heightListerner = new HeightListerner(this);
    }

    public void registerRegionsListener() {
        final boolean isWgEventsEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuardEvents");
        if (isWgEventsEnabled) {
            this.regionListener.registerEvent();
        } else {
            this.myLogger.info("Actions on region enter/leave will be disabled!");
            this.myLogger.info("Please download WorldGuardEvents to enable them.");
            this.myLogger.info("https://www.spigotmc.org/resources/worldguard-events.65176/");
            this.regionListener.unregisterEvent();
        }
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