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
        final double x = this.getNumber(playerLocation.getX(), params, 1, 0, "Cord x");
        final double y = this.getNumber(playerLocation.getY(), params, 2, 90, "Cord y");
        final double z = this.getNumber(playerLocation.getZ(), params, 3, 0, "Cord z");
        final float yaw = (float) this.getNumber(playerLocation.getYaw(), params, 4, 180, "Yaw");
        final float pitch = (float) this.getNumber(playerLocation.getPitch(), params, 5, 0, "Pitch");

        final Location location = new Location(world, x, y, z, yaw, pitch);
        player.teleport(location);
    }

    private World getWorld(final Player player, final String[] params) {
        if (params.length > 0) {
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

    private double getNumber(final double current, final String[] params, final int paramNumb, final int defaultValue, final String specifiedIn) {
        if (params.length > paramNumb) {
            final String param = params[paramNumb];
            if (param.isEmpty()) {
                return defaultValue;
            }

            try {
                return this.calculate(current, param);
            } catch (final NumberFormatException ex) {
                super.plugin.getMyLogger().warning("The value: " + param + " specified in \"" + specifiedIn + "\" for the [TELEPORT] action is invalid. Please check your config file.");
            }
        }

        return defaultValue;
    }

    private double calculate(final double current, final String param) throws NumberFormatException {
        final boolean isRelative = param.charAt(0) == '~';
        final char operation = isRelative ? this.getOperation(param) : param.charAt(0);
        final String valuePart = isRelative ? param.substring(this.getCutAmount(operation)) : this.getNumber(operation, param);

        final double arg = valuePart.isEmpty() ? 0f : Double.parseDouble(valuePart);
        switch (operation) {
            case '+':
                return current + arg;
            case '-':
                return isRelative ? current - arg : -arg;
            case '*':
                return current * arg;
            case '/':
                return arg != 0 ? current / arg : current;
            case '%':
                return arg != 0 ? current % arg : 0;
            case '=':
                return current;
            default:
                return arg;
        }
    }

    private char getOperation(final String param) {
        return param.length() > 1 ? param.charAt(1) : '=';
    }

    private int getCutAmount(final char operation) {
        return 1 + (operation != '=' ? 1 : 0);
    }

    private String getNumber(final char operation, final String param) {
        return Character.isDigit(operation) ? param : param.substring(1);
    }
}
