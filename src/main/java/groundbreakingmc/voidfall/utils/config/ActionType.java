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