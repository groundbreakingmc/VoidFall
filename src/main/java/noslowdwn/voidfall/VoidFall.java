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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        final Plugin wgEvents = Bukkit.getPluginManager().getPlugin("WorldGuardEvents");

        super.getCommand("voidfall").setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("voidfall.reload")) {
                sender.sendMessage(colorizer.colorize(getConfig().getString("messages.no-permission")));
                return true;
            }

            this.configValues.setupValues();
            sender.sendMessage(colorizer.colorize(getConfig().getString("messages.reload-message")));

            if (wgEvents != null && !wgEvents.isEnabled()) {
                debug("[VoidFall] Actions on region enter/leave will be disabled!", null, "info");
                debug("[VoidFall] Please download WorldGuardEvents to enable them.", null, "info");
                debug("[WorldGuardEvents] https://www.spigotmc.org/resources/worldguard-events.65176/", null, "info");
            }

            return true;
        });

        this.getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new YCords(this), this);

        if (wgEvents != null && wgEvents.isEnabled()) {
            this.getServer().getPluginManager().registerEvents(new Region(this), this);
        } else {
            debug("[VoidFall] Actions on region enter/leave will be disabled!", null, "info");
            debug("[VoidFall] Please download WorldGuardEvents to enable them.", null, "info");
            debug("[WorldGuardEvents] https://www.spigotmc.org/resources/worldguard-events.65176/", null, "info");
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> new UpdateChecker(this).checkVersion(), 60L);
    }

    public void debug(String msg, Player p, String type) {
        if (super.getConfig().getBoolean("debug-mode", false)) {
            switch (type) {
                case "info":
                    Bukkit.getLogger().info(colorizer.colorize(msg));
                    break;
                case "warn":
                    Bukkit.getLogger().warning(colorizer.colorize(msg));
                    break;
            }
        }
    }

    private int extractMainVersion(String versionString) {
        Pattern p = Pattern.compile("(?<=MC: |^)(1\\.\\d+)(?=\\D|$)");
        Matcher m = p.matcher(versionString);
        if (m.find()) {
            return Integer.parseInt(m.group().replace("1.", ""));
        } else {
            return 0;
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
        } catch (NumberFormatException ex) {
            super.getLogger().warning("\u001b[32mFailed to extract server version. Plugin may not work correctly!");
            return 0;
        }
    }
}