package groundbreakingmc.voidfall.utils;

import groundbreakingmc.voidfall.VoidFall;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PapiUtil {

    private static boolean isPapiEnabled;

    public static String parse(final Player player, final String message) {
        return isPapiEnabled ? PlaceholderAPI.setPlaceholders(player, message) : message;
    }

    public static void setPapiStatus(final VoidFall plugin) {
        isPapiEnabled = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
}
