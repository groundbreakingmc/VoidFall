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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class VoidFall extends JavaPlugin {

    private ConfigValues configValues;
    private IColorizer colorizer;
    private Actions actionsExecutor;

    @Override
    public void onEnable() {
        this.configValues = new ConfigValues(this);
        this.configValues.setupValues();

        this.colorizer = this.getColorizerByVersion();

        this.actionsExecutor = new Actions(this);


        super.getCommand("voidfall").setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("voidfall.reload")) {
                sender.sendMessage(colorizer.colorize(getConfig().getString("messages.no-permission")));
                return true;
            }

            this.configValues.setupValues();
            sender.sendMessage(colorizer.colorize(getConfig().getString("messages.reload-message")));

            final boolean isWgEventsEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuardEvents");
            if (!isWgEventsEnabled) {
                this.debug("[VoidFall] Actions on region enter/leave will be disabled!", "info");
                this.debug("[VoidFall] Please download WorldGuardEvents to enable them.", "info");
                this.debug("[VoidFall] https://www.spigotmc.org/resources/worldguard-events.65176/", "info");
            }

            return true;
        });

        this.getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new YCords(this), this);

        final boolean isWgEventsEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuardEvents");
        if (isWgEventsEnabled) {
            this.getServer().getPluginManager().registerEvents(new Region(this), this);
        } else {
            this.debug("[VoidFall] Actions on region enter/leave will be disabled!", "info");
            this.debug("[VoidFall] Please download WorldGuardEvents to enable them.", "info");
            this.debug("[VoidFall] https://www.spigotmc.org/resources/worldguard-events.65176/", "info");
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> new UpdateChecker(this).checkVersion(), 60L);
    }

    public void debug(final String message, final String type) {
        if (super.getConfig().getBoolean("debug-mode", false)) {
            switch (type) {
                case "info":
                    Bukkit.getLogger().info(colorizer.colorize(message));
                    break;
                case "warn":
                    Bukkit.getLogger().warning(colorizer.colorize(message));
                    break;
            }
        }
    }

    public IColorizer getColorizerByVersion() {
        final boolean is16OrAbove = this.getSubVersion() >= 16;
        return is16OrAbove
                ? new LegacyColorizer()
                : new VanillaColorizer();
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