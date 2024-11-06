package groundbreakingmc.voidfall.utils.logging;

import groundbreakingmc.voidfall.VoidFall;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class BukkitLogger implements ILogger {

    private final Logger logger;

    public BukkitLogger(final VoidFall plugin) {
        this.logger = plugin.getLogger();
    }

    public void info(final String msg) {
        this.logger.log(Level.INFO, msg);
    }

    public void warning(final String msg) {
        this.logger.log(Level.WARNING, msg);
    }
}
