package noslowdwn.voidfall.actions;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ConsoleCommand extends AbstractAction {

    public ConsoleCommand(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        final Server server = super.plugin.getServer();
        super.plugin.getServer().getScheduler().runTask(super.plugin, () ->
                server.dispatchCommand(server.getConsoleSender(), string)
        );
    }
}
