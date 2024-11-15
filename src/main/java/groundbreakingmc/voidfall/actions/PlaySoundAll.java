package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class PlaySoundAll extends AbstractAction {

    public PlaySoundAll(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        final String[] params = string.split(";", 3);
        final Sound sound = this.getSound(params[0]);
        final float volume = this.getVolume(params);
        final float pitch = this.getPitch(params);
        Bukkit.getOnlinePlayers().forEach(target ->
                target.playSound(target.getLocation(), sound, volume, pitch)
        );
    }

    private Sound getSound(final String param) {
        try {
            return Sound.valueOf(param.toUpperCase());
        } catch (final IllegalArgumentException exception) {
            super.plugin.getMyLogger().warning("The value: " + param + " specified in \"Sound\" for the [PLAY_SOUND] action is invalid. Please check your config file.");
            return Sound.ENTITY_SHULKER_HURT_CLOSED;
        }
    }

    private float getVolume(final String[] params) {
        if (params.length > 1) {
            try {
                return Float.parseFloat(params[1]);
            } catch (final NumberFormatException exception) {
                super.plugin.getMyLogger().warning("The value: " + params[1] + " specified in \"Volume\" for the [PLAY_SOUND] action is invalid. Please check your config file.");
            }
        }
        return 1f;
    }

    private float getPitch(final String[] params) {
        if (params.length > 1) {
            try {
                return Float.parseFloat(params[2]);
            } catch (final NumberFormatException exception) {
                super.plugin.getMyLogger().warning("The value: " + params[2] + " specified in \"Pitch\" for the [PLAY_SOUND] action is invalid. Please check your config file.");
                return 1f;
            }
        }
        return 1f;
    }
}
