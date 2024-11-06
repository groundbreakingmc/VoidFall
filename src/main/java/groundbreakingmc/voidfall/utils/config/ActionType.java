package groundbreakingmc.voidfall.utils.config;

import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.actions.*;

public enum ActionType {
    CONSOLE("[CONSOLE]", (plugin, string, world) -> createAction(plugin, string, world, ConsoleCommand::new, "[CONSOLE]")),
    MESSAGE("[MESSAGE]", (plugin, string, world) -> new SendMessage(plugin, string)),
    BROADCAST("[BROADCAST]", (plugin, string, world) -> new SendBroadcast(plugin, string)),
    PLAYER("[PLAYER]", (plugin, string, world) -> createAction(plugin, string, world, PlayerCommand::new, "[PLAYER]")),
    TITLE("[TITLE]", (plugin, string, world) -> createAction(plugin, string, world, ShowTitle::new, "[TITLE]")),
    ACTIONBAR("[ACTIONBAR]", (plugin, string, world) -> createAction(plugin, string, world, ShowActionbar::new, "[ACTIONBAR]")),
    PLAY_SOUND("[PLAY_SOUND]", (plugin, string, world) -> createAction(plugin, string, world, PlaySound::new, "[PLAY_SOUND]")),
    PLAY_SOUND_ALL("[PLAY_SOUND_ALL]", (plugin, string, world) -> createAction(plugin, string, world, PlaySoundAll::new, "[PLAY_SOUND_ALL]")),
    EFFECT("[EFFECT]", (plugin, string, world) -> createAction(plugin, string, world, GiveEffect::new, "[EFFECT]")),
    TELEPORT("[TELEPORT]", (plugin, string, world) -> createAction(plugin, string, world, TeleportPlayer::new, "[TELEPORT]")),
    GAMEMODE("[GAMEMODE]", (plugin, string, world) -> createAction(plugin, string, world, (pl, str) -> new SetGamemode(pl, str, world), "[GAMEMODE]"));

    private final String prefix;
    private final ActionFactory actionFactory;

    ActionType(final String prefix, final ActionFactory actionFactory) {
        this.prefix = prefix;
        this.actionFactory = actionFactory;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public AbstractAction createAction(final VoidFall plugin, final String string) {
        return this.actionFactory.create(plugin, string.replace(this.prefix, "").trim(), this.prefix);
    }

    public static ActionType fromString(final String string) {
        for (final ActionType actionType : values()) {
            if (string.startsWith(actionType.getPrefix())) {
                return actionType;
            }
        }

        return null;
    }

    @FunctionalInterface
    interface ActionFactory {
        AbstractAction create(final VoidFall plugin, final String string, final String world);
    }

    private static AbstractAction createAction(final VoidFall plugin, final String string, final String world, final ActionCreator actionCreator, final String prefix) {
        if (string.isEmpty()) {
            logMissingArguments(plugin, world, prefix);
            return null;
        }

        return actionCreator.create(plugin, string);
    }

    private static void logMissingArguments(final VoidFall plugin, final String world, final String prefix) {
        plugin.getMyLogger().warning("Missing arguments for " + prefix + " action. Check your config file.");
        plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
    }

    @FunctionalInterface
    interface ActionCreator {
        AbstractAction create(final VoidFall plugin, final String string);
    }
}



//public final class Actions {
//
//    private final VoidFall plugin;
//    private final IColorizer colorizer;
//
//    public Actions(final VoidFall plugin) {
//        this.plugin = plugin;
//        this.colorizer = this.plugin.getColorizer();
//    }
//
//    public AbstractAction getAction(String string, final String world) {
//        if (string.startsWith("[CONSOLE] ")) {
//            string = string.replace("[CONSOLE]", "").trim();
//            if (string.isEmpty()) {
//                plugin.getMyLogger().warning("Missing command for [CONSOLE] action. Check your config file.");
//                plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
//            } else {
//                return new ConsoleCommand(this.plugin, string);
//            }
//        } else if (string.startsWith("[MESSAGE] ")) {
//            string = colorizer.colorize(string.replace("[MESSAGE]", "").trim());
//            return new SendMessage(this.plugin, string);
//        } else if (string.startsWith("[BROADCAST] ")) {
//            string = colorizer.colorize(string.replace("[BROADCAST]", "").trim());
//            return new SendBroadcast(this.plugin, string);
//        } else if (string.startsWith("[PLAYER] ")) {
//            string = string.replace("[PLAYER]", "").trim();
//            if (string.isEmpty()) {
//                this.plugin.getMyLogger().warning("Missing command for [PLAYER] action. Check your config file.");
//                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
//            } else {
//                return new PlayerCommand(this.plugin, string);
//            }
//        } else if (string.startsWith("[TITLE] ")) {
//            string = colorizer.colorize(string.replace("[TITLE]", "").trim());
//            if (string.isEmpty()) {
//                this.plugin.getMyLogger().warning("Missing arguments for [TITLE] action. Check your config file.");
//                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
//            } else {
//                return new ShowTitle(this.plugin, string);
//            }
//        } else if (string.startsWith("[ACTIONBAR] ")) {
//            string = colorizer.colorize(string.replace("[ACTIONBAR]", "").trim());
//            if (string.isEmpty()) {
//                this.plugin.getMyLogger().warning("Missing arguments for [ACTIONBAR] action. Check your config file.");
//                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
//            } else {
//                return new ShowActionbar(this.plugin, string);
//            }
//        } else if (string.startsWith("[PLAY_SOUND] ")) {
//            string = string.replace("[PLAY_SOUND]", "").trim();
//            if (string.isEmpty()) {
//                this.plugin.getMyLogger().warning("Missing arguments for [PLAY_SOUND] action! Check your config file.");
//                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
//            } else {
//                return new PlaySound(this.plugin, string);
//            }
//        }  else if (string.startsWith("[PLAY_SOUND_ALL] ")) {
//            string = string.replace("[PLAY_SOUND_ALL]", "").trim();
//            if (string.isEmpty()) {
//                this.plugin.getMyLogger().warning("Missing arguments for [PLAY_SOUND_ALL] action! Check your config file.");
//                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
//            } else {
//                return new PlaySoundAll(this.plugin, string);
//            }
//        } else if (string.startsWith("[EFFECT] ")) {
//            string = string.replace("[EFFECT]", "").trim();
//            if (string.isEmpty()) {
//                plugin.getMyLogger().warning("Missing arguments for [EFFECT] action! Check your config file.");
//                plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
//            } else {
//                return new GiveEffect(this.plugin, string);
//            }
//        } else if (string.startsWith("[TELEPORT] ")) {
//            string = string.replace("[TELEPORT]", "").trim();
//            if (string.isEmpty()) {
//                this.plugin.getMyLogger().warning("Missing arguments for [TELEPORT] action! Check your config file.");
//                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
//            } else {
//                return new TeleportPlayer(this.plugin, string);
//            }
//        } else if (string.startsWith("[GAMEMODE] ")) {
//            string = string.replace("[GAMEMODE]", "").trim();
//            if (string.isEmpty()) {
//                this.plugin.getMyLogger().warning("Missing arguments for [GAMEMODE] action! Check your config file.");
//                this.plugin.getMyLogger().warning("Path to: worlds." + world + ".execute-commands");
//            } else {
//                return new SetGamemode(this.plugin, string, world);
//            }
//        } else {
//            this.plugin.getMyLogger().warning("&cYou're trying to cause an action that doesn't exist.");
//            this.plugin.getMyLogger().warning("&cPath to: worlds." + world + ".execute-commands");
//            this.plugin.getMyLogger().warning("&cAction: " + string);
//        }
//
//        return null;
//    }
//}
