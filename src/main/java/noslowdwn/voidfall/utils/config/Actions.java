package noslowdwn.voidfall.utils.config;

import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.actions.*;
import noslowdwn.voidfall.utils.colorizer.IColorizer;

public class Actions {

    private final VoidFall plugin;
    private final IColorizer colorizer;

    public Actions(final VoidFall plugin) {
        this.plugin = plugin;
        this.colorizer = this.plugin.getColorizer();
    }

    public AbstractAction getAction(String string, final String world) {
        if (string.startsWith("[CONSOLE] ")) {
            string = string.replace("[CONSOLE]", "").trim();
            if (string.isEmpty()) {
                plugin.getMyLogger().warning("Missing command for [CONSOLE] action. Check your config file.");
                plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
            } else {
                return new ConsoleCommand(this.plugin, string);
            }
        } else if (string.startsWith("[MESSAGE] ")) {
            string = colorizer.colorize(string.replace("[MESSAGE]", "").trim());
            return new SendMessage(this.plugin, string);
        } else if (string.startsWith("[BROADCAST] ")) {
            string = colorizer.colorize(string.replace("[BROADCAST]", "").trim());
            return new SendBroadcast(this.plugin, string);
        } else if (string.startsWith("[PLAYER] ")) {
            string = string.replace("[PLAYER]", "").trim();
            if (string.isEmpty()) {
                this.plugin.getMyLogger().warning("Missing command for [PLAYER] action. Check your config file.");
                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
            } else {
                return new PlayerCommand(this.plugin, string);
            }
        } else if (string.startsWith("[TITLE] ")) {
            string = colorizer.colorize(string.replace("[TITLE]", "").trim());
            if (string.isEmpty()) {
                this.plugin.getMyLogger().warning("Missing arguments for [TITLE] action. Check your config file.");
                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
            } else {
                return new ShowTitle(this.plugin, string);
            }
        } else if (string.startsWith("[ACTIONBAR] ")) {
            string = colorizer.colorize(string.replace("[ACTIONBAR]", "").trim());
            if (string.isEmpty()) {
                this.plugin.getMyLogger().warning("Missing arguments for [ACTIONBAR] action. Check your config file.");
                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
            } else {
                return new ShowActionbar(this.plugin, string);
            }
        } else if (string.startsWith("[PLAY_SOUND] ")) {
            string = string.replace("[PLAY_SOUND]", "").trim();
            if (string.isEmpty()) {
                this.plugin.getMyLogger().warning("Missing arguments for [PLAY_SOUND] action! Check your config file.");
                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
            } else {
                return new PlaySound(this.plugin, string);
            }
        }  else if (string.startsWith("[PLAY_SOUND_ALL] ")) {
            string = string.replace("[PLAY_SOUND_ALL]", "").trim();
            if (string.isEmpty()) {
                this.plugin.getMyLogger().warning("Missing arguments for [PLAY_SOUND_ALL] action! Check your config file.");
                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
            } else {
                return new PlaySoundAll(this.plugin, string);
            }
        } else if (string.startsWith("[EFFECT] ")) {
            string = string.replace("[EFFECT]", "").trim();
            if (string.isEmpty()) {
                plugin.getMyLogger().warning("Missing arguments for [EFFECT] action! Check your config file.");
                plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
            } else {
                return new GiveEffect(this.plugin, string);
            }
        } else if (string.startsWith("[TELEPORT] ")) {
            string = string.replace("[TELEPORT]", "").trim();
            if (string.isEmpty()) {
                this.plugin.getMyLogger().warning("Missing arguments for [TELEPORT] action! Check your config file.");
                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
            } else {
                return new TeleportPlayer(this.plugin, string);
            }
        } else if (string.startsWith("[GAMEMODE] ")) {
            string = string.replace("[GAMEMODE]", "").trim();
            if (string.isEmpty()) {
                this.plugin.getMyLogger().warning("Missing arguments for [GAMEMODE] action! Check your config file.");
                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
            } else {
                return new SetGamemode(this.plugin, string, world);
            }
        } else {
            this.plugin.getMyLogger().warning("&cYou're trying to cause an action that doesn't exist.");
            this.plugin.getMyLogger().warning("&cPath to: worlds." + world + ".execute-commands");
            this.plugin.getMyLogger().warning("&cAction: " + string);
        }

        return null;
    }
}
