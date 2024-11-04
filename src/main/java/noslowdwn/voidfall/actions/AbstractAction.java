package noslowdwn.voidfall.actions;

import lombok.AllArgsConstructor;
import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.StringUtil;
import org.bukkit.entity.Player;

@AllArgsConstructor
public abstract class AbstractAction {

    protected final VoidFall plugin;
    private final String string;

    private static final String[] placeholders = { "%player%", "%world%", "%world_display_name%" };

    public void run(final Player player) {
        final String playerName = player.getName();
        final String worldName = player.getWorld().getName();
        final String worldDisplayName = this.plugin.getConfigValues().getWorldDisplayName().getOrDefault(worldName, worldName);
        final String[] replacement = { playerName, worldName, worldDisplayName };
        final String formattedString = StringUtil.replaceEach(string, placeholders, replacement);
        this.process(player, formattedString);
    }

    abstract void process(Player player, String string);

}
