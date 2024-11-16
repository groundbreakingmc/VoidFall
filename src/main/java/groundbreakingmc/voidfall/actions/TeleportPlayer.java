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
        final String[] params = string.split(";");

        final Location playerLocation = player.getLocation();
        final World world = this.getWorld(player, params);
        final double x = this.getCord(playerLocation.getX(), params, 1, 0, "Cord x");
        final double y = this.getCord(playerLocation.getY(), params, 2, 90, "Cord y");
        final double z = this.getCord(playerLocation.getZ(), params, 3, 0, "Cord z");
        final float yaw = this.getLook(playerLocation.getYaw(), params, 4, 180f, "Yaw");
        final float pitch = this.getLook(playerLocation.getPitch(), params, 5, 0f, "Pitch");

        final Location location = new Location(world, x, y, z, yaw, pitch);
        player.teleport(location);
    }

    private World getWorld(final Player player, final String[] params) {
        if (params.length > 0){
            final String param = params[0];
            if (param.equals("~")) {
                return player.getLocation().getWorld();
            } else {
                final World tpWorld = super.plugin.getServer().getWorld(param);
                if (tpWorld != null) {
                    return tpWorld;
                }
                super.plugin.getMyLogger().warning("The value: " + param + " specified in \"World\" for the [TELEPORT] action is invalid. Please check your config file.");
            }
        }
        return super.plugin.getServer().getWorlds().get(0);
    }

    private double getCord(final double current, final String[] params, final int paramNumb, final int defaultValue, final String specifiedIn) {
        if (params.length > paramNumb) {
            final String param = params[paramNumb];
            if (param.isEmpty()) {
                return defaultValue;
            }

            try {
                final boolean isRelative = param.charAt(0) == '~';
                final char operation = isRelative ? (param.length() > 1 ? param.charAt(1) : '=') : param.charAt(0);
                final String valuePart = isRelative ? param.substring(1 + (operation != '=' ? 1 : 0)) : (Character.isDigit(operation) ? param : param.substring(1));

                final double arg = valuePart.isEmpty() ? 0f : Double.parseDouble(valuePart);
                switch (operation) {
                    case '+': return current + arg;
                    case '-': return isRelative ? current - arg : -arg;
                    case '*': return current * arg;
                    case '/': return arg != 0 ? current / arg : current;
                    case '%': return arg != 0 ? current % arg : 0;
                    case '=': return current;
                    default:  return arg;
                }
            } catch (NumberFormatException ex) {
                super.plugin.getMyLogger().warning("The value: " + param + " specified in \"" + specifiedIn + "\" for the [TELEPORT] action is invalid. Please check your config file.");
            }
        }

        return defaultValue;
    }

    private float getLook(final float current, final String[] params, final int paramNumb, final float defaultValue, final String specifiedIn) {
        if (params.length > paramNumb) {
            final String param = params[paramNumb];
            if (param.isEmpty()) {
                return defaultValue;
            }

            try {
                final boolean isRelative = param.charAt(0) == '~';
                final char operation = isRelative ? (param.length() > 1 ? param.charAt(1) : '=') : param.charAt(0);
                final String valuePart = isRelative ? param.substring(1 + (operation != '=' ? 1 : 0)) : (Character.isDigit(operation) ? param : param.substring(1));

                final float arg = valuePart.isEmpty() ? 0f : Float.parseFloat(valuePart);
                switch (operation) {
                    case '+': return current + arg;
                    case '-': return isRelative ? current - arg : -arg;
                    case '*': return current * arg;
                    case '/': return arg != 0 ? current / arg : current;
                    case '%': return arg != 0 ? current % arg : 0;
                    case '=': return current;
                    default:  return arg;
                }
            } catch (NumberFormatException ex) {
                super.plugin.getMyLogger().warning("The value: " + param + " specified in \"" + specifiedIn + "\" for the [TELEPORT] action is invalid. Please check your config file.");
            }
        }

        return defaultValue;
    }
}
