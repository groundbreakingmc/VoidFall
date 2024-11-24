package com.github.groundbreakingmc.voidfall.actions;

import com.github.groundbreakingmc.voidfall.VoidFall;
import org.bukkit.entity.Player;

public final class PlayerCommand extends AbstractAction {
    
    public PlayerCommand(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        player.chat("/" + string);
    }
}
