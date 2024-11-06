package groundbreakingmc.voidfall.listeners.wgevents;

import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.actions.AbstractAction;
import groundbreakingmc.voidfall.constructors.RegionConstructor;
import groundbreakingmc.voidfall.utils.config.ConfigValues;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public final class LeaveRegion implements Listener {

    private final ConfigValues configValues;

    private final String[] placeholders = {"%player%", "%region%"};

    private boolean isRegistered;

    public LeaveRegion(final VoidFall plugin) {
        this.configValues = plugin.getConfigValues();
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
}
