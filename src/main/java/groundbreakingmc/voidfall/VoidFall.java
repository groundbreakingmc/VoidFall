package groundbreakingmc.voidfall;

import groundbreakingmc.voidfall.listeners.player.MoveListener;
import groundbreakingmc.voidfall.listeners.player.DeathListener;
import groundbreakingmc.voidfall.listeners.player.JoinListener;
import groundbreakingmc.voidfall.listeners.player.QuitListener;
import groundbreakingmc.voidfall.listeners.player.RespawnListener;
import groundbreakingmc.voidfall.listeners.wgevents.EntryRegion;
import groundbreakingmc.voidfall.listeners.wgevents.LeaveRegion;
import groundbreakingmc.voidfall.utils.PapiUtil;
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
    private RespawnListener respawnListener;
    private EntryRegion entryRegionListener;
    private LeaveRegion leaveRegionListener;
    private MoveListener moveListener;

    private boolean isWgEventsEnabled;

    @Override
    public void onEnable() {
        new Metrics(this, 23829);

        this.configValues = new ConfigValues(this);

        this.registerListenerClasses();

        final int subVersion = this.getSubVersion();
        this.myLogger = this.getLoggerByVersion(subVersion);

        PapiUtil.setPapiStatus(this);

        this.configValues.setupValues();
        this.registerCommand();

        this.isWgEventsEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuardEvents");
        if (!isWgEventsEnabled) {
            this.myLogger.info("Actions on region enter/leave will be disabled!");
            this.myLogger.info("Please download WorldGuardEvents to enable them.");
            this.myLogger.info("https://www.spigotmc.org/resources/worldguard-events.65176/");
        }
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
        this.respawnListener = new RespawnListener(this);
        this.entryRegionListener = new EntryRegion(this);
        this.leaveRegionListener = new LeaveRegion(this);
        this.moveListener = new MoveListener(this);
    }

    public void setupColorizer(final String colorizerMode) {
        switch (colorizerMode.toUpperCase()) {
            case "MINIMESSAGE":
                this.colorizer = new MiniMessageColorizer();
                break;
            case "LEGACY":
                this.colorizer = new LegacyColorizer();
                break;
            case "LEGACY_ADVANCED":
                this.colorizer = new LegacyAdvancedColorizer();
                break;
            default:
                this.colorizer = new VanillaColorizer();
                break;
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