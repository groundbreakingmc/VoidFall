package noslowdwn.voidfall.handlers;

import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.config.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class YCords implements Listener {

    private final Set<Player> executing = new HashSet<>();

    private final VoidFall plugin;
    private final ConfigValues configValues;
    private final Actions actionsExecutor;

    public YCords(final VoidFall plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
        this.actionsExecutor = plugin.getActionsExecutor();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        if (executing.contains(p)) {
            return;
        }

        final String world = p.getWorld().getName();
        if (!this.configValues.containsWorld(world)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                final double pHeight = p.getLocation().getY();

                if (executing.contains(p)) {
                    return;
                }
                if (configValues.worldHasFloorMode(world) && pHeight <= configValues.worldFloorHeight(world)) {
                    remove(p, world);
                    run(p, world, "floor");
                } else if (configValues.worldHasRoofMode(world) && pHeight >= configValues.worldRoofHeight(world)) {
                    remove(p, world);
                    run(p, world, "roof");
                }
            }

            private void remove(Player p, String world) {
                Bukkit.getScheduler().runTaskLater(
                        plugin,
                        () -> executing.remove(p),
                        configValues.getWorldRepeatFix(world) * 20L
                );
            }

            private void run(Player p, String world, String mode) {
                final List<String> commands = configValues.getWorldCommands(world, mode);

                if (commands.isEmpty()) {
                    plugin.debug("Nothing to execute because commands list are empty!", "warn");
                    plugin.debug("Path to: worlds." + world + "." + mode + ".execute-commands", "warn");
                    return;
                }

                executing.add(p);

                if (configValues.isWorldRunModeRandom(world, mode)) {
                    actionsExecutor.executeRandom(p, commands, world, "worlds");
                } else {
                    for (String str : commands) {
                        actionsExecutor.execute(p, str, world, "worlds");
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}