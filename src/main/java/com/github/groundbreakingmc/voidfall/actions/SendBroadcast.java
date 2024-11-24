package com.github.groundbreakingmc.voidfall.actions;

import com.github.groundbreakingmc.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SendBroadcast extends AbstractAction {

    public SendBroadcast(final VoidFall plugin, final String string) {
        super(plugin, plugin.getColorizer().colorize(string));
    }

    @Override
    public void process(final Player player, final String string) {
        Bukkit.getOnlinePlayers().forEach(target ->
                target.sendMessage(string)
        );
    }
}
