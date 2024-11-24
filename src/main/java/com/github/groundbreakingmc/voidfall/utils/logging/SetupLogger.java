package com.github.groundbreakingmc.voidfall.utils.logging;

import com.github.groundbreakingmc.voidfall.VoidFall;

public class SetupLogger {

    private final VoidFall plugin;

    public SetupLogger(final VoidFall plugin) {
        this.plugin = plugin;
    }

    public ILogger getLogger() {
        final boolean is19OrAbove = this.getSubVersion() >= 19;
        return is19OrAbove && this.isPaperOrFork() ? new PaperLogger(this.plugin) : new BukkitLogger(this.plugin);
    }

    private int getSubVersion() {
        try {
            return Integer.parseInt(this.plugin.getServer().getVersion().split("\\.")[1]);
        } catch (final NumberFormatException ex) {
            this.plugin.getLogger().warning("Failed to extract server version. Plugin may not work correctly!");
            return 0;
        }
    }

    private boolean isPaperOrFork() {
        try {
            Class.forName("com.destroystokyo.paper.utils.PaperPluginLogger");
            return true;
        } catch (ClassNotFoundException ignore) {
            return false;
        }
    }
}
