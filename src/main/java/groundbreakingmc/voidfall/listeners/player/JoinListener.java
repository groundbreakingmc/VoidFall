package groundbreakingmc.voidfall.listeners.player;

import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.actions.AbstractAction;
import groundbreakingmc.voidfall.utils.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public final class JoinListener implements Listener {

    private final ConfigValues configValues;

    private final String[] placeholders = {"%player%"};

    private boolean isRegistered;

    public JoinListener(final VoidFall plugin) {
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler
    public void onQuit(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String[] replacement = {player.getName()};
        final List<AbstractAction> actions = this.configValues.getPlayerServerJoinActions();
        for (int i = 0; i < actions.size(); i++) {
            final AbstractAction action = actions.get(i);
            action.run(player, placeholders, replacement);
        }
    }
}
