package noslowdwn.voidfall.listeners;

import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.actions.AbstractAction;
import noslowdwn.voidfall.utils.config.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Region implements Listener {

    private final VoidFall plugin;
    private final ConfigValues configValues;

    public Region(final VoidFall plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionEntered(RegionEnteredEvent e) {
        if (configValues.isRegionsEmpty()) {
            return;
        }

        final Player player = Bukkit.getPlayer(e.getUUID());
        if (player == null) {
            return;
        }

        final String regionName = e.getRegionName();
        final String worldName = player.getWorld().getName();

        if (!configValues.containsEnterRegionWorld(regionName, worldName)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                final List<String> commands = configValues.getEntryRegionActions(regionName);

                if (commands.isEmpty()) {
                    plugin.getMyLogger().warning("Nothing to execute because commands list are empty!");
                    plugin.getMyLogger().warning("Path to: regions." + regionName + ".on-enter.execute-commands");
                    return;
                }

                if (configValues.entryRegionsAreUsingRandom(regionName)) {
                    actionsExecutor.executeRandom(player, commands, worldName, "regions");
                } else {
                    for (String cmd : commands) {
                        actionsExecutor.execute(player, cmd, worldName, "regions");
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionLeave(RegionLeftEvent e) {
        if (configValues.isRegionsEmpty()) {
            return;
        }

        final Player player = Bukkit.getPlayer(e.getUUID());
        if (player == null) {
            return;
        }

        final String regionName = e.getRegionName();
        final String worldName = player.getWorld().getName();

        if (!configValues.containsLeaveRegionWorld(regionName, worldName)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                final List<AbstractAction> commands = configValues.getLeaveRegionActions(regionName);

                if (!commands.isEmpty()) {
                    if (configValues.leaveRegionsAreUsingRandom(regionName)) {
                        actionsExecutor.executeRandom(player, commands, worldName, "regions");
                    } else {
                        for (String cmd : commands) {
                            actionsExecutor.execute(player, cmd, worldName, "regions");
                        }
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}