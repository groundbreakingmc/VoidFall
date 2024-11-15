package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.entity.Player;

public final class ShowTitle extends AbstractAction {

    public ShowTitle(final VoidFall plugin, final String string) {
        super(plugin, plugin.getColorizer().colorize(string));
    }

    @Override
    public void process(final Player player, final String string) {
        final String[] params = string.split(";", 5);
        final String title = params[0];
        final String subtitle = params.length > 1 ? params[1] : "";
        final int fadeIn = this.getNumb(params, 4, 10, "FadeIn");
        final int stay = this.getNumb(params, 3, 40, "Stay");
        final int fadeOut = this.getNumb(params, 2, 15, "fadeOut");
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    private int getNumb(final String[] params, final int paramNumb, final int defaultValue, final String specifiedIn) {
        if (params.length > paramNumb) {
            try {
                return Integer.parseInt(params[paramNumb]);
            } catch (NumberFormatException e) {
                super.plugin.getMyLogger().warning("The value: " + params[paramNumb] + " specified in \"" + specifiedIn + "\" for the [TITLE] action is invalid. Please check your config file.");
            }
        }

        return defaultValue;
    }
}
