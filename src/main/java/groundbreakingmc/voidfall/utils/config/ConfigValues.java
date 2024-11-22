package groundbreakingmc.voidfall.utils.config;

import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.actions.AbstractAction;
import groundbreakingmc.voidfall.constructors.RegionConstructor;
import groundbreakingmc.voidfall.constructors.WorldsConstructor;
import groundbreakingmc.voidfall.listeners.RegisterUtil;
import groundbreakingmc.voidfall.listeners.player.MoveListener;
import groundbreakingmc.voidfall.listeners.player.DeathListener;
import groundbreakingmc.voidfall.listeners.player.JoinListener;
import groundbreakingmc.voidfall.listeners.player.QuitListener;
import groundbreakingmc.voidfall.listeners.player.RespawnListener;
import groundbreakingmc.voidfall.listeners.wgevents.EntryRegion;
import groundbreakingmc.voidfall.listeners.wgevents.LeaveRegion;
import groundbreakingmc.voidfall.utils.UpdatesChecker;
import groundbreakingmc.voidfall.utils.colorizer.IColorizer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

@Getter
public final class ConfigValues {

    private final Map<String, WorldsConstructor> worlds = new HashMap<>();
    private final Map<String, RegionConstructor> regions = new HashMap<>();

    private boolean isInstantlyRespawnEnabled;

    private boolean isPlayerServerJoinTriggerRandom;
    private boolean isPlayerServerQuitTriggerRandom;
    private boolean isPlayerDeathTriggerRandom;
    private boolean isPlayerRespawnTriggerRandom;

    private final List<AbstractAction> playerServerJoinActions = new ArrayList<>();
    private final List<AbstractAction> playerServerQuitActions = new ArrayList<>();
    private final List<AbstractAction> playerDeathActions = new ArrayList<>();
    private final List<AbstractAction> playerRespawnActions = new ArrayList<>();

    // Messages
    private String noPermissionMessage;
    private String reloadMessage;
    private String usageError;

    private final Map<String, String> worldDisplayName = new HashMap<>();

    private final VoidFall plugin;

    public ConfigValues(final VoidFall plugin) {
        this.plugin = plugin;
    }

    public void setupValues() {
        final FileConfiguration config = new ConfigLoader(this.plugin).loadAndGet("config", 1.6);

        this.setupSettings(config);
        this.setupWorldActions(config);
        this.setupRegionActions(config);
        this.setupPlayerActions(config);
        this.setupMessages(config);
    }

    private void setupSettings(final FileConfiguration config) {
        final ConfigurationSection settingsSection = config.getConfigurationSection("settings");
        if (settingsSection != null) {

            final ConfigurationSection updateSection = settingsSection.getConfigurationSection("updates");
            if (updateSection != null) {
                final boolean checkForUpdates = updateSection.getBoolean("check");
                if (!checkForUpdates) {
                    this.plugin.getMyLogger().warning("Updates checker was disabled, but it's not recommend by the author to do it!");
                } else {
                    final boolean downloadUpdate = updateSection.getBoolean("auto-update");

                    Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () ->
                            new UpdatesChecker(this.plugin).check(downloadUpdate, false),
                            200L
                    );
                }
            }

            final String colorizerMode = settingsSection.getString("colorizer-serializer");
            this.plugin.setupColorizer(colorizerMode);
        }
    }

    private void setupWorldActions(final FileConfiguration config) {
        this.worlds.clear();
        final ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
        final MoveListener moveListener = this.plugin.getMoveListener();
        if (worldsSection != null) {
            for (final String worldName : worldsSection.getKeys(false)) {
                final ConfigurationSection worldSection = worldsSection.getConfigurationSection(worldName);

                boolean roofEnabled = false;
                int roofExecuteHeight = 666;
                int roofRepeatFix = 3;
                boolean roofRandom = false;
                final List<AbstractAction> roofCommands = new ArrayList<>();
                final ConfigurationSection roofSection = worldSection.getConfigurationSection("roof");
                if (roofSection != null) {
                    roofEnabled = true;
                    roofExecuteHeight = roofSection.getInt("executing-height");
                    roofRepeatFix = roofSection.getInt("repeat-fix");
                    roofRandom = roofSection.getBoolean("random");
                    final List<String> commands = roofSection.getStringList("execute-commands");
                    for (int i = 0; i < commands.size(); i++) {
                        final String command = commands.get(i);
                        final ActionType actionType = ActionType.fromString(command);
                        if (actionType != null) {
                            final AbstractAction action = actionType.createAction(this.plugin, command);
                            roofCommands.add(action);
                        }
                    }
                }

                boolean floorEnabled = false;
                int floorExecuteHeight = 0;
                int floorRepeatFix = 3;
                boolean floorRandom = false;
                final List<AbstractAction> floorCommands = new ArrayList<>();
                final ConfigurationSection floorSection = worldSection.getConfigurationSection("floor");
                if (floorSection != null) {
                    floorEnabled = true;
                    floorExecuteHeight = floorSection.getInt("executing-height");
                    floorRepeatFix = floorSection.getInt("repeat-fix");
                    floorRandom = floorSection.getBoolean("random");
                    final List<String> commands = floorSection.getStringList("execute-commands");
                    for (int i = 0; i < commands.size(); i++) {
                        final String command = commands.get(i);
                        final ActionType actionType = ActionType.fromString(command);
                        if (actionType != null) {
                            final AbstractAction action = actionType.createAction(this.plugin, command);
                            floorCommands.add(action);
                        }
                    }
                }

                if (!roofCommands.isEmpty() || !floorCommands.isEmpty()) {
                    final WorldsConstructor world = WorldsConstructor.builder()
                            .roofModeEnabled(roofEnabled)
                            .roofExecuteHeight(roofExecuteHeight)
                            .roofRepeatFix(roofRepeatFix)
                            .roofRandom(roofRandom)
                            .roofActions(roofCommands)
                            .floorModeEnabled(floorEnabled)
                            .floorExecuteHeight(floorExecuteHeight)
                            .floorRepeatFix(floorRepeatFix)
                            .floorRandom(floorRandom)
                            .floorActions(floorCommands)
                            .build();

                    this.worlds.put(worldName, world);
                }
            }

            if (!this.worlds.isEmpty()) {
                RegisterUtil.register(this.plugin, moveListener);
                return;
            }
        } else {
            this.plugin.getMyLogger().warning("Failed to load section \"worlds\" from file \"config.yml\". Please check your configuration file, or delete it and restart your server!");
            this.plugin.getMyLogger().warning("If you think this is a plugin error, leave a issue on the https://github.com/grounbreakingmc/VoidFall/issues");
        }
        RegisterUtil.unregister(moveListener);
    }

    private void setupRegionActions(final FileConfiguration config) {
        this.regions.clear();
        final ConfigurationSection regionsSection = config.getConfigurationSection("regions");
        final EntryRegion entryRegion = this.plugin.getEntryRegionListener();
        final LeaveRegion leaveRegion = this.plugin.getLeaveRegionListener();
        if (regionsSection != null) {
            boolean enter = false;
            boolean leave = false;
            for (String regionName : regionsSection.getKeys(false)) {
                final ConfigurationSection keySection = regionsSection.getConfigurationSection(regionName);

                boolean enterRandom = false;
                final List<AbstractAction> enterActions = new ArrayList<>();
                final ConfigurationSection enterSection = regionsSection.getConfigurationSection("on-enter");
                if (enterSection != null) {
                    enterRandom = enterSection.getBoolean("random");
                    final List<String> executeCommands = enterSection.getStringList("execute-commands");
                    for (int i = 0; i < executeCommands.size(); i++) {
                        final String command = executeCommands.get(i);
                        final ActionType actionType = ActionType.fromString(command);
                        if (actionType != null) {
                            final AbstractAction action = actionType.createAction(this.plugin, command);
                            enterActions.add(action);
                        }
                    }
                    enter = true;
                }

                boolean leaveRandom = false;
                final List<AbstractAction> leaveActions = new ArrayList<>();
                final ConfigurationSection leaveSection = regionsSection.getConfigurationSection("on-leave");
                if (leaveSection != null) {
                    leaveRandom = leaveSection.getBoolean("random");
                    final List<String> executeCommands = leaveSection.getStringList("execute-commands");
                    for (int i = 0; i < executeCommands.size(); i++) {
                        final String command = executeCommands.get(i);
                        final ActionType actionType = ActionType.fromString(command);
                        if (actionType != null) {
                            final AbstractAction action = actionType.createAction(this.plugin, command);
                            leaveActions.add(action);
                        }
                    }
                    leave = true;
                }

                if (!enterActions.isEmpty() || !leaveActions.isEmpty()) {
                    final RegionConstructor region = RegionConstructor.builder()
                            .worlds(new HashSet<>(keySection.getStringList("worlds")))
                            .enterRandom(enterRandom)
                            .enterActions(enterActions)
                            .leaveRandom(leaveRandom)
                            .leaveActions(leaveActions)
                            .build();

                    this.regions.put(regionName, region);
                }
            }

            if (!this.regions.isEmpty()) {
                if (enter) {
                    RegisterUtil.register(this.plugin, entryRegion);
                } else {
                    RegisterUtil.unregister(entryRegion);
                }

                if (leave) {
                    RegisterUtil.register(this.plugin, leaveRegion);
                } else {
                    RegisterUtil.unregister(leaveRegion);
                }
            }
        } else {
            this.plugin.getMyLogger().warning("Failed to load section \"regions\" from file \"config.yml\". Please check your configuration file, or delete it and restart your server!");
            this.plugin.getMyLogger().warning("If you think this is a plugin error, leave a issue on the https://github.com/grounbreakingmc/VoidFall/issues");
            RegisterUtil.unregister(entryRegion);
            RegisterUtil.unregister(leaveRegion);
        }
    }

    private void setupPlayerActions(final FileConfiguration config) {
        final ConfigurationSection player = config.getConfigurationSection("player");
        if (player != null) {
            this.setupOnJoin(player);
            this.setupOnQuit(player);
            this.setupOnDeath(player);
            this.setupOnRespawn(player);
        } else {
            this.plugin.getMyLogger().warning("Failed to load section \"player\" from file \"config.yml\". Please check your configuration file, or delete it and restart your server!");
            this.plugin.getMyLogger().warning("If you think this is a plugin error, leave a issue on the https://github.com/grounbreakingmc/VoidFall/issues");
        }
    }

    private void setupOnJoin(final ConfigurationSection playerSection) {
        this.playerServerJoinActions.clear();
        final ConfigurationSection joinSection = playerSection.getConfigurationSection("on-server-join");
        final JoinListener joinListener = this.plugin.getJoinListener();
        if (joinSection != null) {
            this.isPlayerServerJoinTriggerRandom = joinSection.getBoolean("random");
            final List<String> joinCommands = joinSection.getStringList("execute-commands");
            if (!joinCommands.isEmpty()) {
                for (int i = 0; i < joinCommands.size(); i++) {
                    final String command = joinCommands.get(i);
                    final ActionType actionType = ActionType.fromString(command);
                    if (actionType != null) {
                        final AbstractAction action = actionType.createAction(this.plugin, command);
                        this.playerServerJoinActions.add(action);
                    }
                }
                if (!this.playerServerJoinActions.isEmpty()) {
                    RegisterUtil.register(this.plugin, joinListener);
                    return;
                }
            }
        }

        RegisterUtil.unregister(joinListener);
    }

    private void setupOnQuit(final ConfigurationSection playerSection) {
        this.playerServerQuitActions.clear();
        final ConfigurationSection quitSection = playerSection.getConfigurationSection("on-server-leave");
        final QuitListener quitListener = this.plugin.getQuitListener();
        if (quitSection != null) {
            this.isPlayerServerQuitTriggerRandom = quitSection.getBoolean("random");
            final List<String> quitCommands = quitSection.getStringList("execute-commands");
            if (!quitCommands.isEmpty()) {
                for (int i = 0; i < quitCommands.size(); i++) {
                    final String command = quitCommands.get(i);
                    final ActionType actionType = ActionType.fromString(command);
                    if (actionType != null) {
                        final AbstractAction action = actionType.createAction(this.plugin, command);
                        this.playerServerQuitActions.add(action);
                    }
                }
                if (!this.playerServerQuitActions.isEmpty()) {
                    RegisterUtil.register(this.plugin, quitListener);
                    return;
                }
            }
        }

        RegisterUtil.unregister(quitListener);
    }

    private void setupOnDeath(final ConfigurationSection playerSection) {
        this.playerDeathActions.clear();
        final ConfigurationSection deathSection = playerSection.getConfigurationSection("on-death");
        final DeathListener deathListener = this.plugin.getDeathListener();
        if (deathSection != null) {
            this.isPlayerDeathTriggerRandom = deathSection.getBoolean("random");
            this.isInstantlyRespawnEnabled = deathSection.getBoolean("instantly-respawn");
            final List<String> deathCommands = deathSection.getStringList("execute-commands");
            if (!deathCommands.isEmpty()) {
                for (int i = 0; i < deathCommands.size(); i++) {
                    final String command = deathCommands.get(i);
                    final ActionType actionType = ActionType.fromString(command);
                    if (actionType != null) {
                        final AbstractAction action = actionType.createAction(this.plugin, command);
                        this.playerDeathActions.add(action);
                    }
                }
                if (!this.playerDeathActions.isEmpty()) {
                    RegisterUtil.register(this.plugin, deathListener);
                    return;
                }
            }
        }

        RegisterUtil.unregister(deathListener);
    }

    private void setupOnRespawn(final ConfigurationSection playerSection) {
        this.playerRespawnActions.clear();
        final ConfigurationSection respawnSection = playerSection.getConfigurationSection("on-respawn");
        final RespawnListener respawnListener = this.plugin.getRespawnListener();
        if (respawnSection != null) {
            this.isPlayerRespawnTriggerRandom = respawnSection.getBoolean("random");
            final List<String> respawnCommands = respawnSection.getStringList("execute-commands");
            if (!respawnCommands.isEmpty()) {
                for (int i = 0; i < respawnCommands.size(); i++) {
                    final String command = respawnCommands.get(i);
                    final ActionType actionType = ActionType.fromString(command);
                    if (actionType != null) {
                        final AbstractAction action = actionType.createAction(this.plugin, command);
                        this.playerRespawnActions.add(action);
                    }
                }
                if (!this.playerRespawnActions.isEmpty()) {
                    RegisterUtil.register(this.plugin, respawnListener);
                    return;
                }
            }
        }

        RegisterUtil.unregister(respawnListener);
    }

    private void setupMessages(final FileConfiguration config) {
        final ConfigurationSection messages = config.getConfigurationSection("messages");
        if (messages != null) {
            final IColorizer colorizer = this.plugin.getColorizer();
            noPermissionMessage = colorizer.colorize(messages.getString("no-permission"));
            reloadMessage = colorizer.colorize(messages.getString("reload-message"));
            usageError = colorizer.colorize(messages.getString("usage-error"));
            worldDisplayName.clear();
            final ConfigurationSection namesSection = messages.getConfigurationSection("worlds-display-names");
            for (final String key : namesSection.getKeys(false)) {
                worldDisplayName.put(key, namesSection.getString(key));
            }
        } else {
            this.plugin.getMyLogger().warning("Failed to load section \"messages\" from file \"config.yml\". Please check your configuration file, or delete it and restart your server!");
            this.plugin.getMyLogger().warning("If you think this is a plugin error, leave a issue on the https://github.com/grounbreakingmc/VoidFall/issues");
        }
    }
}
