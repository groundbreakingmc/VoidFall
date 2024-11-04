package noslowdwn.voidfall.listeners;

import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.actions.AbstractAction;
import noslowdwn.voidfall.utils.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class QuitListener implements Listener {

    private final VoidFall plugin;
    private final ConfigValues configValues;

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
        final List<AbstractAction> actions = this.configValues.getPlayerServerQuitActions();
        for (int i = 0; i < actions.size(); i++) {
            final AbstractAction action = actions.get(i);
            action.run(player);
        }
    }
}
