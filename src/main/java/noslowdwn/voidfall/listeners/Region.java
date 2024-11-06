package noslowdwn.voidfall.listeners;

import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.actions.AbstractAction;
import noslowdwn.voidfall.constructors.RegionConstructor;
import noslowdwn.voidfall.utils.config.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public final class Region implements Listener {

    private final VoidFall plugin;
    private final ConfigValues configValues;

    private final String[] placeholders = {"%player%", "%region%"};

    private boolean isRegistered;

    public Region(final VoidFall plugin) {
        this.plugin = plugin;
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

    @EventHandler
    public void onRegionLeave(final RegionLeftEvent event) {
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

        final List<AbstractAction> actions = region.leaveActions;
        for (int i = 0; i < actions.size(); i++) {
            final AbstractAction action = actions.get(i);
            action.run(player, placeholders, replacements);
        }
    }

    public void registerEvent() {
        this.unregisterEvent();

        this.plugin.getServer().getPluginManager().registerEvent(
                RegionEnteredEvent.class,
                this,
                EventPriority.MONITOR,
                (listener, event) -> this.onRegionEntered((RegionEnteredEvent) event),
                this.plugin
        );

        this.plugin.getServer().getPluginManager().registerEvent(
                RegionLeftEvent.class,
                this,
                EventPriority.MONITOR,
                (listener, event) -> this.onRegionLeave((RegionLeftEvent) event),
                this.plugin
        );

        this.isRegistered = true;
    }

    public void unregisterEvent() {
        if (!this.isRegistered) {
            HandlerList.unregisterAll(this);
            this.isRegistered = false;
        }
    }
}