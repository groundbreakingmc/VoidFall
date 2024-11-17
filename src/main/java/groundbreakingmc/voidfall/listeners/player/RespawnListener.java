package groundbreakingmc.voidfall.listeners.player;

import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.actions.AbstractAction;
import groundbreakingmc.voidfall.utils.config.ConfigValues;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class RespawnListener implements Listener {

    private final ConfigValues configValues;

    private final String[] placeholders = {"%player%"};

    private boolean isRegistered;

    public RespawnListener(final VoidFall plugin) {
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final String[] replacement = {player.getName()};
        final List<AbstractAction> actions = this.configValues.getPlayerServerQuitActions();
        for (int i = 0; i < actions.size(); i++) {
            final AbstractAction action = actions.get(i);
            action.run(player, placeholders, replacement);
        }
    }

}
