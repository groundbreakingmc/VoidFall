package com.github.groundbreakingmc.voidfall.actions;

import com.github.groundbreakingmc.voidfall.VoidFall;
import org.bukkit.entity.Player;

public final class ShowActionbar extends AbstractAction {

    public ShowActionbar(final VoidFall plugin, final String string) {
        super(plugin, plugin.getColorizer().colorize(string));
    }

    @Override
    public void process(final Player player, final String string) {
        player.sendActionBar(string);
    }
}
