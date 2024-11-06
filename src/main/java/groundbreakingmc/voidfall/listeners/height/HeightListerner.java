package groundbreakingmc.voidfall.listeners.height;

import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.actions.AbstractAction;
import groundbreakingmc.voidfall.constructors.WorldsConstructor;
import groundbreakingmc.voidfall.utils.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public final class HeightListerner implements Listener {

    private final VoidFall plugin;
    private final ConfigValues configValues;

    private final String[] placeholders = { "%player%", "%world%", "%world_display_name%" };

    private boolean isRegistered;

    public HeightListerner(final VoidFall plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final String worldName = player.getWorld().getName();
        final WorldsConstructor world = this.configValues.getWorlds().get(worldName);
        if (world == null) {
            return;
        }

        final List<AbstractAction> actions;

        final String playerName = player.getName();
        final double playerHeight = player.getLocation().getY();

        if (world.floorModeEnabled && playerHeight <= world.floorExecuteHeight) {
            if (world.executingFloorContains(this.plugin, playerName)) {
                return;
            }

            actions = world.floorActions;
        } else if (world.roofModeEnabled && playerHeight <= world.roofExecuteHeight) {
            if (world.executingRoofContains(this.plugin, playerName)) {
                return;
            }

            actions = world.roofActions;
        } else {
            return;
        }

        final String[] replacements = this.getReplacements(player);
        for (int i = 0; i < actions.size(); i++) {
            final AbstractAction action = actions.get(i);
            action.run(player, this.placeholders, replacements);
        }
    }

    public String[] getReplacements(final Player player) {
        final String playerName = player.getName();
        final String worldName = player.getWorld().getName();
        final String worldDisplayName = this.plugin.getConfigValues().getWorldDisplayName().getOrDefault(worldName, worldName);

        return new String[]{ playerName, worldName, worldDisplayName };
    }
}