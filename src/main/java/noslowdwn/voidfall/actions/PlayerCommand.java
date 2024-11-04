package noslowdwn.voidfall.actions;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getScheduler;

public class PlayerCommand extends AbstractAction {
    
    public PlayerCommand(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        getScheduler().runTask(super.plugin, () ->
                player.chat("/" + string)
        );
    }
}
