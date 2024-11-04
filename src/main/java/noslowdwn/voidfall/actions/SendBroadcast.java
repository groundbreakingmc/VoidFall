package noslowdwn.voidfall.actions;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SendBroadcast extends AbstractAction {

    public SendBroadcast(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        Bukkit.getOnlinePlayers().forEach(player1 ->
                player1.sendMessage(string)
        );
    }
}
