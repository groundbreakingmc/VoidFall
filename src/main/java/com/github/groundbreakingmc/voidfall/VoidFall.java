package com.github.groundbreakingmc.voidfall;

import com.github.groundbreakingmc.voidfall.commands.VoidFallCommand;
import com.github.groundbreakingmc.voidfall.listeners.player.*;
import com.github.groundbreakingmc.voidfall.listeners.wgevents.EntryRegion;
import com.github.groundbreakingmc.voidfall.listeners.wgevents.LeaveRegion;
import com.github.groundbreakingmc.voidfall.utils.PapiUtil;
import com.github.groundbreakingmc.voidfall.utils.colorizer.*;
import com.github.groundbreakingmc.voidfall.utils.config.ConfigValues;
import com.github.groundbreakingmc.voidfall.utils.logging.ILogger;
import com.github.groundbreakingmc.voidfall.utils.logging.SetupLogger;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class VoidFall extends JavaPlugin {

    private ConfigValues configValues;

    private IColorizer colorizer;

    private final ILogger myLogger = new SetupLogger(this).getLogger();

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
        final PluginCommand command = super.getCommand("voidfall");
        final VoidFallCommand handler = new VoidFallCommand(this);
        command.setExecutor(handler);
        command.setTabCompleter(handler);
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
}