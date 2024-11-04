package noslowdwn.voidfall.actions;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.entity.Player;

public class SendMessage extends AbstractAction {

    public SendMessage(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        player.sendMessage(string);
    }
}
