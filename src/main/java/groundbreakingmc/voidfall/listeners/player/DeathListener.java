package groundbreakingmc.voidfall.listeners.player;

import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.utils.config.ConfigValues;
import groundbreakingmc.voidfall.actions.AbstractAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class DeathListener implements Listener {

    private final VoidFall plugin;
    private final ConfigValues configValues;

    private final String[] placeholders = { "%player%", "%world_display_name%" };

    private boolean isRegistered;

    public DeathListener(final VoidFall plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler
    public void onQuit(final PlayerDeathEvent event) {
        final Player player = event.getEntity();

        if (configValues.isInstantlyRespawnEnabled()) {
            new BukkitRunnable() {
                public void run() {
                    player.spigot().respawn();
                }
            }.runTaskLater(plugin, 1L);
        }

        final String worldName = player.getWorld().getName();
        final String worldDisplayName = this.configValues.getWorldDisplayName().getOrDefault(worldName, worldName);
        final String[] replacement = { player.getName(), worldDisplayName };

        final List<AbstractAction> actions = this.configValues.getPlayerDeathActions();
        for (int i = 0; i < actions.size(); i++) {
            final AbstractAction action = actions.get(i);
            action.run(player, this.placeholders, replacement);
        }
    }
}
