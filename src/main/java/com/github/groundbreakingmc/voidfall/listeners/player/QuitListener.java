package com.github.groundbreakingmc.voidfall.listeners.player;

import com.github.groundbreakingmc.voidfall.VoidFall;
import com.github.groundbreakingmc.voidfall.actions.AbstractAction;
import com.github.groundbreakingmc.voidfall.utils.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public final class QuitListener implements Listener {

    private final ConfigValues configValues;

    private final String[] placeholders = { "%player%", "%world_display_name%" };

    private boolean isRegistered;

    public QuitListener(final VoidFall plugin) {
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final String worldName = player.getWorld().getName();
        final String worldDisplayName = this.configValues.getWorldDisplayName().getOrDefault(worldName, worldName);
        final String[] replacement = { player.getName(), worldDisplayName };

        final List<AbstractAction> actions = this.configValues.getPlayerServerQuitActions();
        for (int i = 0; i < actions.size(); i++) {
            final AbstractAction action = actions.get(i);
            action.run(player, placeholders, replacement);
        }
    }
}
