package noslowdwn.voidfall.listeners;

import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.actions.AbstractAction;
import noslowdwn.voidfall.utils.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public final class QuitListener implements Listener {

    private final VoidFall plugin;
    private final ConfigValues configValues;

    private final String[] placeholders = {"%player%"};

    private boolean isRegistered;

    public QuitListener(final VoidFall plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        this.processEvent(event);
    }

    public void registerEvent() {
        if (this.isRegistered) {
            this.unregisterEvent();
        }

        this.plugin.getServer().getPluginManager().registerEvent(
                PlayerQuitEvent.class,
                this,
                EventPriority.LOW,
                (listener, event) -> this.onQuit((PlayerQuitEvent) event),
                this.plugin
        );

        this.isRegistered = true;
    }

    private void unregisterEvent() {
        HandlerList.unregisterAll(this);
        this.isRegistered = false;
    }

    private void processEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final String[] replacement = {player.getName()};
        final List<AbstractAction> actions = this.configValues.getPlayerServerQuitActions();
        for (int i = 0; i < actions.size(); i++) {
            final AbstractAction action = actions.get(i);
            action.run(player, placeholders, replacement);
        }
    }
}