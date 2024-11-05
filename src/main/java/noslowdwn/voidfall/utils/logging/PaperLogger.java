package noslowdwn.voidfall.utils.logging;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import noslowdwn.voidfall.VoidFall;

public final class PaperLogger implements ILogger {

    private final ComponentLogger logger;
    private final LegacyComponentSerializer legacySection;

    public PaperLogger(final VoidFall plugin) {
        this.logger = ComponentLogger.logger(plugin.getLogger().getName());
        this.legacySection = LegacyComponentSerializer.legacySection();
    }

    public void info(final String msg) {
        this.logger.info(legacySection.deserialize(msg));
    }

    public void warning(final String msg) {
        this.logger.warn(legacySection.deserialize(msg));
    }
}
