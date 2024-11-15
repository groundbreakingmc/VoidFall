package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public final class ConsoleCommand extends AbstractAction {

    public ConsoleCommand(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        final Server server = super.plugin.getServer();
        server.dispatchCommand(server.getConsoleSender(), string);
    }
}
