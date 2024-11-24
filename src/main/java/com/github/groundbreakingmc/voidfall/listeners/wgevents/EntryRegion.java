package com.github.groundbreakingmc.voidfall.listeners.wgevents;

import com.github.groundbreakingmc.voidfall.VoidFall;
import com.github.groundbreakingmc.voidfall.actions.AbstractAction;
import com.github.groundbreakingmc.voidfall.constructors.RegionConstructor;
import com.github.groundbreakingmc.voidfall.utils.config.ConfigValues;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public final class EntryRegion implements Listener {

    private final ConfigValues configValues;

    private final String[] placeholders = {"%player%", "%region%"};

    private boolean isRegistered;

    public EntryRegion(final VoidFall plugin) {
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler
    public void onRegionEntered(final RegionEnteredEvent event) {
        final Player player = Bukkit.getPlayer(event.getUUID());
        if (player == null) {
            return;
        }

        final String regionName = event.getRegionName();
        final String worldName = player.getWorld().getName();

        final RegionConstructor region = configValues.getRegions().get(regionName);
        if (region == null || !region.worlds.contains(worldName)) {
            return;
        }

        final String[] replacements = {player.getName(), regionName};

        final List<AbstractAction> actions = region.enterActions;
        for (int i = 0; i < actions.size(); i++) {
            final AbstractAction action = actions.get(i);
            action.run(player, placeholders, replacements);
        }
    }
}
