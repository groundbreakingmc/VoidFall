package groundbreakingmc.voidfall.listeners.player;

import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.actions.AbstractAction;
import groundbreakingmc.voidfall.utils.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class RespawnListener implements Listener {

    private final VoidFall plugin;
    private final ConfigValues configValues;

    private final String[] placeholders = { "%player%", "%world_display_name%" };

    private boolean isRegistered;

    public RespawnListener(final VoidFall plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        final String worldName = player.getWorld().getName();
        final String worldDisplayName = this.configValues.getWorldDisplayName().getOrDefault(worldName, worldName);
        final String[] replacement = { player.getName(), worldDisplayName };

        final List<AbstractAction> actions = this.configValues.getPlayerRespawnActions();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < actions.size(); i++) {
                    final AbstractAction action = actions.get(i);
                    action.run(player, placeholders, replacement);
                }
            }
        }.runTaskLater(this.plugin, 5L);
    }

}
