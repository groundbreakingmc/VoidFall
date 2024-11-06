package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class TeleportPlayer extends AbstractAction {
    
    public TeleportPlayer(final VoidFall plugin, final String string) {
        super(plugin, string);
    }

    @Override
    public void process(final Player player, final String string) {
        final String[] params = string.split(";", 6);

        World tpWorld = null;
        double x = 0, y = 90, z = 0;
        float yaw = 180f, pitch = 0f;

        switch (params.length) {
            case 6:
                if (params[5].charAt(0) == '~') {
                    float arg = Float.parseFloat(params[5].replace("~", ""));
                    yaw = player.getLocation().getYaw() + arg;
                } else {
                    try {
                        yaw = Float.parseFloat(params[5]);
                    } catch (final NumberFormatException e) {
                        super.plugin.getMyLogger().warning("The value: " + params[5] + " specified in \"Yaw\" for the [TELEPORT] action is invalid. Please check your config file.");
                    }
                }
            case 5:
                if (params[4].charAt(0) == '~') {
                    float arg = Float.parseFloat(params[4].replace("~", ""));
                    pitch = player.getLocation().getPitch() + arg;
                } else {
                    try {
                        pitch = Float.parseFloat(params[4]);
                    } catch (final NumberFormatException e) {
                        super.plugin.getMyLogger().warning("The value: " + params[4] + " specified in \"Pitch\" for the [TELEPORT] action is invalid. Please check your config file.");
                    }
                }
            case 4:
                if (params[3].charAt(0) == '~') {
                    float arg = Float.parseFloat(params[3].replace("~", ""));
                    z = player.getLocation().getZ() + arg;
                } else {
                    try {
                        z = Double.parseDouble(params[3]);
                    } catch (final NumberFormatException e) {
                        super.plugin.getMyLogger().warning("The value: " + params[3] + " specified in \"Cord z\" for the [TELEPORT] action is invalid. Please check your config file.");
                    }
                }
            case 3:
                if (params[2].charAt(0) == '~') {
                    float arg = Float.parseFloat(params[2].replace("~", ""));
                    y = player.getLocation().getY() + arg;
                } else {
                    try {
                        y = Double.parseDouble(params[2]);
                    } catch (final NumberFormatException e) {
                        super.plugin.getMyLogger().warning("The value: " + params[2] + " specified in \"Cord y\" for the [TELEPORT] action is invalid. Please check your config file.");
                    }
                }
            case 2:
                if (params[1].charAt(0) == '~') {
                    float arg = Float.parseFloat(params[1].replace("~", ""));
                    x = player.getLocation().getX() + arg;
                } else {
                    try {
                        x = Double.parseDouble(params[1]);
                    } catch (final NumberFormatException e) {
                        super.plugin.getMyLogger().warning("The value: " + params[1] + " specified in \"Cord x\" for the [TELEPORT] action is invalid. Please check your config file.");
                    }
                }
            case 1:
                if (params[0].equals("~")) {
                    tpWorld = player.getLocation().getWorld();
                    if (tpWorld == null) {
                        super.plugin.getMyLogger().warning("The value: " + params[0] + " specified in \"World\" for the [TELEPORT] action is invalid. Please check your config file.");
                        tpWorld = super.plugin.getServer().getWorlds().get(0);
                    }
                } else {
                    tpWorld = super.plugin.getServer().getWorld(params[0]);
                }
            default:
                final Location location = tpWorld.getSpawnLocation();
                location.setX(x);
                location.setY(y);
                location.setZ(z);
                location.setPitch(pitch);
                location.setYaw(yaw);
                super.plugin.getServer().getScheduler().runTask(super.plugin, () ->
                    player.teleport(location)
                );
        }

    }
}
