package noslowdwn.voidfall.actions;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySoundAll extends AbstractAction {

    public PlaySoundAll(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        final String[] params = string.split(";", 3);

        Sound sound = Sound.ENTITY_SHULKER_HURT_CLOSED;
        float volume = 1f;
        float pitch = 1f;
        switch (params.length) {
            case 3:
                try {
                    pitch = Float.parseFloat(params[2]);
                } catch (final NumberFormatException exception) {
                    super.plugin.getMyLogger().warning("The value: " + params[2] + " specified in \"Pitch\" for the [PLAY_SOUND_ALL] action is invalid. Please check your config file.");
                }
            case 2:
                try {
                    volume = Float.parseFloat(params[1]);
                } catch (final NumberFormatException exception) {
                    super.plugin.getMyLogger().warning("The value: " + params[1] + " specified in \"Volume\" for the [PLAY_SOUND_ALL] action is invalid. Please check your config file.");
                }
            case 1:
                try {
                    sound = Sound.valueOf(params[0].toUpperCase());
                } catch (final IllegalArgumentException exception) {
                    super.plugin.getMyLogger().warning("The value: " + params[0] + " specified in \"Sound\" for the [PLAY_SOUND_ALL] action is invalid. Please check your config file.");
                }
            default:
                final Sound fSound = sound;
                float fVolume = volume, fPitch = pitch;
                Bukkit.getOnlinePlayers().forEach(player1 ->
                        player1.playSound(player1.getLocation(), fSound, fVolume, fPitch)
                );
        }
    }
}
