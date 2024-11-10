package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.entity.Player;

public final class SendMessage extends AbstractAction {

    public SendMessage(final VoidFall plugin, final String string) {
        super(plugin, plugin.getColorizer().colorize(string));
    }

    @Override
    public void process(final Player player, final String string) {
        player.sendMessage(string);
    }
}
