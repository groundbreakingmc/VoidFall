package noslowdwn.voidfall.actions;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.Bukkit.getScheduler;

public final class GiveEffect extends AbstractAction {

    public GiveEffect(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        final String[] params = string.split(";", 3);

        PotionEffectType effect = PotionEffectType.BLINDNESS;
        int duration = 60;
        int amplifier = 1;
        switch (params.length) {
            case 3:
                try {
                    duration = Integer.parseInt(params[2]) * 20;
                } catch (final NumberFormatException e) {
                    super.plugin.getMyLogger().warning("The value: " + params[2] + " specified in \"Duration\" for the [EFFECT] action is invalid. Please check your config file.");
                }
            case 2:
                try {
                    amplifier = Integer.parseInt(params[2]);
                } catch (final NumberFormatException e) {
                    super.plugin.getMyLogger().warning("The value: " + params[1] + " specified in \"Amplifier\" for the [EFFECT] action is invalid. Please check your config file.");
                }
            case 1:
                effect = PotionEffectType.getByName(params[0]);
                if (effect == null) {
                    super.plugin.getMyLogger().warning("The value: " + params[0] + " specified in \"PotionEffect\" for the [EFFECT] action is invalid. Please check your config file.");
                    effect = PotionEffectType.BLINDNESS;
                }
            default:
                final PotionEffectType fEffect = effect;
                int fDuration = duration;
                int fAmplifier = amplifier;
                getScheduler().runTask(super.plugin, () ->
                    player.addPotionEffect(new PotionEffect(fEffect, fDuration, fAmplifier))
                );
        }

    }
}
