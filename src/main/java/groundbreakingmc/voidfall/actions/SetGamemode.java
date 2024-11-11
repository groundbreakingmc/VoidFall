package groundbreakingmc.voidfall.actions;

import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class SetGamemode extends AbstractAction {

    private final String world;

    public SetGamemode(final VoidFall plugin, final String string, final String world) {
        super(plugin, string);
        this.world = world;
    }

    @Override
    public void process(final Player player, final String string) {
        final GameMode gameMode;
        switch (string.toLowerCase()) {
            case "1":
            case "creative":
                gameMode = GameMode.CREATIVE;
                break;
            case "2":
            case "adventure":
                gameMode = GameMode.ADVENTURE;
                break;
            case "3":
            case "spectator":
                gameMode = GameMode.SPECTATOR;
                break;
            case "0":
            case "survival":
                gameMode = GameMode.SURVIVAL;
                break;
            default:
                if (this.world != null) {
                    super.plugin.getMyLogger().warning("The gamemode given for [GAMEMODE]: " + string + ", doesn't exist or not valid!");
                    super.plugin.getMyLogger().warning("&cPath to: worlds." + this.world + ".execute-commands");
                }
                return;
        }

        player.setGameMode(gameMode);
    }
}
