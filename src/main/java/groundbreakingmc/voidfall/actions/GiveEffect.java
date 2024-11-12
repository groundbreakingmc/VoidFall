package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class GiveEffect extends AbstractAction {

    public GiveEffect(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        final String[] params = string.split(";", 3);
        final PotionEffectType effect = this.getEffect(params[0]);
        final int duration = this.getDuration(params);
        final int amplifier = this.getAmplifier(params);
        final boolean ambient = params.length <= 3 || Boolean.parseBoolean(params[3]);
        final boolean particles = params.length <= 4 || Boolean.parseBoolean(params[4]);
        final Color color = this.getColor(params);
        player.addPotionEffect(new PotionEffect(effect, duration, amplifier, ambient, particles, color));
    }

    private PotionEffectType getEffect(final String param) {
        PotionEffectType effect = PotionEffectType.getByName(param);
        if (effect == null) {
            super.plugin.getMyLogger().warning("The value: " + param + " specified in \"PotionEffect\" for the [EFFECT] action is invalid. Please check your config file.");
            effect = PotionEffectType.BLINDNESS;
        }
        return effect;
    }

    private int getAmplifier(final String[] params) {
        if (params.length > 0) {
            try {
                return Integer.parseInt(params[1]);
            } catch (final NumberFormatException ignore) {
                super.plugin.getMyLogger().warning("The value: " + params[1] + " specified in \"Amplifier\" for the [EFFECT] action is invalid. Please check your config file.");
            }
        }
        return 1;
    }

    private int getDuration(final String[] params) {
        if (params.length > 1) {
            try {
                return Integer.parseInt(params[2]) * 20;
            } catch (final NumberFormatException ignore) {
                super.plugin.getMyLogger().warning("The value: " + params[2] + " specified in \"Duration\" for the [EFFECT] action is invalid. Please check your config file.");
            }
        }
        return 60;
    }

    private Color getColor(final String[] params) {
        if (params.length > 5) {
            try {
                final String[] colors = params[4].split("\\.");
                final int red = Integer.parseInt(colors[0]);
                final int green = Integer.parseInt(colors[1]);
                final int blue = Integer.parseInt(colors[2]);
                return Color.fromRGB(red, green, blue);
            } catch (final NumberFormatException ignore) {
                super.plugin.getMyLogger().warning("The value: " + params[2] + " specified in \"Duration\" for the [EFFECT] action is invalid. Please check your config file.");
            }
        }
        return null;
    }
}
