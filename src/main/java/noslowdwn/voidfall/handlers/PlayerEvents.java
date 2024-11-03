package noslowdwn.voidfall.handlers;

import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PlayerEvents implements Listener {

    private final VoidFall plugin;
    private final ConfigValues configValues;
    private final Actions actionsExecutor;

    public PlayerEvents(final VoidFall plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
        this.actionsExecutor = plugin.getActionsExecutor();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        //if (values.containsPlayerActionsList("on-server-join")) return;
        final Player p = e.getPlayer();

        if (p == null) {
            return;
        }
        if (!this.configValues.isPlayerServerJoinTriggerEnabled()) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                final List<String> commands = configValues.getPlayerServerJoinCommands();

                if (commands.isEmpty()) {
                    plugin.debug("Nothing to execute because commands list are empty!", "warn");
                    plugin.debug("Path to: player.on-server-join.execute-commands", "warn");
                    return;
                }

                if (configValues.isPlayerServerJoinTriggerRandom()) {
                    actionsExecutor.executeRandom(p, commands, p.getWorld().toString(), "player");
                } else {
                    for (String cmd : commands) {
                        actionsExecutor.execute(p, cmd, p.getWorld().toString(), "player");
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        //if (values.containsPlayerActionsList("on-server-leave")) return;
        final Player p = e.getPlayer();

        if (p == null) {
            return;
        }
        if (!configValues.isPlayerServerQuitTriggerEnabled()) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                final List<String> commands = configValues.getPlayerServerQuitCommands();

                if (commands.isEmpty()) {
                    plugin.debug("Nothing to execute because commands list are empty!", "warn");
                    plugin.debug("Path to: player.on-server-leave.execute-commands", "warn");
                    return;
                }

                if (configValues.isPlayerServerQuitTriggerRandom()) {
                    actionsExecutor.executeRandom(p, commands, p.getWorld().toString(), "player");
                } else {
                    for (String cmd : commands) {
                        actionsExecutor.execute(p, cmd, p.getWorld().toString(), "player");
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDeathEvent(PlayerDeathEvent e) {
        //if (values.containsPlayerActionsList("on-death")) return;
        final Player p = e.getEntity().getPlayer();

        if (p == null || !configValues.isPlayerDeathTriggerEnabled()) {
            return;
        }

        if (configValues.isInstantlyRespawnEnabled()) {
            new BukkitRunnable() {
                public void run() {
                    p.spigot().respawn();
                }
            }.runTaskLater(plugin, 1L);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                final List<String> commands = configValues.getPlayerDeathCommands();

                if (commands.isEmpty()) {
                    plugin.debug("Nothing to execute because commands list are empty!", "warn");
                    plugin.debug("Path to: player.on-server-leave.execute-commands", "warn");
                    return;
                }

                if (configValues.isPlayerDeathTriggerRandom()) {
                    actionsExecutor.executeRandom(p, commands, p.getWorld().toString(), "player");
                } else {
                    for (String cmd : commands) {
                        actionsExecutor.execute(p, cmd, p.getWorld().toString(), "player");
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
