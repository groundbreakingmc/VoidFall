package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getScheduler;

public final class ShowTitle extends AbstractAction {

    public ShowTitle(final VoidFall plugin, final String string) {
        super(plugin, plugin.getColorizer().colorize(string));
    }

    @Override
    public void process(final Player player, final String string) {
        final String[] params = string.split(";", 5);

        String main = "", sub = "";
        int fadeIn = 10, stay = 40, fadeOut = 15;
        switch (params.length) {
            case 5:
                try {
                    fadeOut = Integer.parseInt(params[4]);
                } catch (NumberFormatException e) {
                    super.plugin.getMyLogger().warning("The value: " + params[4] + " specified in \"FadeOut\" for the [TITLE] action is invalid. Please check your config file.");
                }
            case 4:
                try {
                    stay = Integer.parseInt(params[3]);
                } catch (NumberFormatException e) {
                    super.plugin.getMyLogger().warning("The value: " + params[3] + " specified in \"Stay\" for the [TITLE] action is invalid. Please check your config file.");
                }
            case 3:
                try {
                    stay = Integer.parseInt(params[2]);
                } catch (NumberFormatException e) {
                    super.plugin.getMyLogger().warning("The value: " + params[2] + " specified in \"FadeIn\" for the [TITLE] action is invalid. Please check your config file.");
                }
            case 2:
                sub = params[1];
            case 1:
                main = params[0];
            default:
                final String text = main, subText = sub;
                final int stayTime = stay, fadeOutTime = fadeOut;

                player.sendTitle(text, subText, fadeIn, stayTime, fadeOutTime);
        }
    }
}
