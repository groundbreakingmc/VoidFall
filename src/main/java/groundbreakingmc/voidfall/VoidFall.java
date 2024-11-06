package groundbreakingmc.voidfall;

import groundbreakingmc.voidfall.listeners.height.HeightListerner;
import groundbreakingmc.voidfall.listeners.player.DeathListener;
import groundbreakingmc.voidfall.listeners.player.JoinListener;
import groundbreakingmc.voidfall.listeners.player.QuitListener;
import groundbreakingmc.voidfall.listeners.wgevents.EntryRegion;
import groundbreakingmc.voidfall.listeners.wgevents.LeaveRegion;
import groundbreakingmc.voidfall.utils.UpdatesChecker;
import groundbreakingmc.voidfall.utils.colorizer.*;
import groundbreakingmc.voidfall.utils.config.ConfigValues;
import groundbreakingmc.voidfall.utils.logging.BukkitLogger;
import groundbreakingmc.voidfall.utils.logging.ILogger;
import groundbreakingmc.voidfall.utils.logging.PaperLogger;
import lombok.Getter;
import me.clip.placeholderapi.metrics.bukkit.Metrics;
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
    private EntryRegion entryRegionListener;
    private LeaveRegion leaveRegionListener;
    private HeightListerner heightListerner;

    private boolean isWgEventsEnabled;

    @Override
    public void onEnable() {
        new Metrics(this, 23829);

        this.configValues = new ConfigValues(this);

        this.registerListenerClasses();
        this.configValues.setupValues();

        final int subVersion = this.getSubVersion();
        this.colorizer = this.getColorizerByVersion();
        this.myLogger = this.getLoggerByVersion(subVersion);

        this.registerCommand();

        this.isWgEventsEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuardEvents");
        if (!isWgEventsEnabled) {
            this.myLogger.info("Actions on region enter/leave will be disabled!");
            this.myLogger.info("Please download WorldGuardEvents to enable them.");
            this.myLogger.info("https://www.spigotmc.org/resources/worldguard-events.65176/");
        }

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
        this.entryRegionListener = new EntryRegion(this);
        this.leaveRegionListener = new LeaveRegion(this);
        this.heightListerner = new HeightListerner(this);
    }

    public IColorizer getColorizerByVersion() {
        final String colorizerMode = super.getConfig().getString("settings.messages-serializer").toUpperCase();

        switch (colorizerMode) {
            case "MINIMESSAGE":
                return new MiniMessageColorizer();
            case "LEGACY":
                return new LegacyColorizer();
            case "LEGACY_ADVANCED":
                return new LegacyAdvancedColorizer();
            default:
                return new VanillaColorizer();
        }
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