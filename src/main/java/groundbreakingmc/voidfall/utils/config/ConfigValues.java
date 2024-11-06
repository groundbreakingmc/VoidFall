package groundbreakingmc.voidfall.utils.config;

import groundbreakingmc.voidfall.constructors.RegionConstructor;
import groundbreakingmc.voidfall.listeners.height.HeightListerner;
import lombok.Getter;
import groundbreakingmc.voidfall.VoidFall;
import groundbreakingmc.voidfall.actions.AbstractAction;
import groundbreakingmc.voidfall.constructors.WorldsConstructor;
import groundbreakingmc.voidfall.listeners.RegisterUtil;
import groundbreakingmc.voidfall.listeners.player.DeathListener;
import groundbreakingmc.voidfall.listeners.player.JoinListener;
import groundbreakingmc.voidfall.listeners.player.QuitListener;
import groundbreakingmc.voidfall.listeners.wgevents.EntryRegion;
import groundbreakingmc.voidfall.listeners.wgevents.LeaveRegion;
import groundbreakingmc.voidfall.utils.colorizer.IColorizer;
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

    private final List<AbstractAction> playerServerJoinActions = new ArrayList<>();
    private final List<AbstractAction> playerServerQuitActions = new ArrayList<>();
    private final List<AbstractAction> playerDeathActions = new ArrayList<>();

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
        final FileConfiguration config = new ConfigLoader(this.plugin).loadAndGet("config", 1.0);
        final Actions actions = new Actions(this.plugin);

        this.setupWorldActions(config, actions);
        this.setupRegionActions(config, actions);
        this.setupPlayerActions(config, actions);
        this.setupMessages(config);
    }

    private void setupWorldActions(final FileConfiguration config, final Actions actions) {
        final ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
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
                        final AbstractAction action = actions.getAction(command, worldName);
                        roofCommands.add(action);
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
                        final AbstractAction action = actions.getAction(command, worldName);
                        floorCommands.add(action);
                    }
                }

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

            final HeightListerner heightListerner = this.plugin.getHeightListerner();
            if (!this.worlds.isEmpty()) {
                RegisterUtil.register(this.plugin, heightListerner);
            } else {
                RegisterUtil.unregister(heightListerner);
            }
        } else {
            this.plugin.getMyLogger().warning("Failed to load section \"worlds\" from file \"config.yml\". Please check your configuration file, or delete it and restart your server!");
            this.plugin.getMyLogger().warning("If you think this is a plugin error, leave a issue on the https://github.com/grounbreakingmc/VoidFall/issues");
        }
    }

    private void setupRegionActions(final FileConfiguration config, final Actions actions) {
        this.regions.clear();
        final ConfigurationSection regionsSection = config.getConfigurationSection("regions");
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
                        final String string = executeCommands.get(i);
                        final AbstractAction action = actions.getAction(string, regionName);
                        enterActions.add(action);
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
                        final String string = executeCommands.get(i);
                        final AbstractAction action = actions.getAction(string, regionName);
                        leaveActions.add(action);
                    }
                    leave = true;
                }

                final RegionConstructor region = RegionConstructor.builder()
                        .worlds(new HashSet<>(keySection.getStringList("worlds")))
                        .enterRandom(enterRandom)
                        .enterActions(enterActions)
                        .leaveRandom(leaveRandom)
                        .leaveActions(leaveActions)
                        .build();

                this.regions.put(regionName, region);
            }

            final EntryRegion entryRegion = this.plugin.getEntryRegionListener();
            if (enter) {
                RegisterUtil.register(this.plugin, entryRegion);
            } else {
                RegisterUtil.unregister(entryRegion);
            }

            final LeaveRegion leaveRegion = this.plugin.getLeaveRegionListener();
            if (leave) {
                RegisterUtil.register(this.plugin, leaveRegion);
            } else {
                RegisterUtil.unregister(leaveRegion);
            }
        } else {
            this.plugin.getMyLogger().warning("Failed to load section \"regions\" from file \"config.yml\". Please check your configuration file, or delete it and restart your server!");
            this.plugin.getMyLogger().warning("If you think this is a plugin error, leave a issue on the https://github.com/grounbreakingmc/VoidFall/issues");
        }
    }

    private void setupPlayerActions(final FileConfiguration config, final Actions actions) {
        final ConfigurationSection player = config.getConfigurationSection("player");
        if (player != null) {
            this.setupOnJoin(player, actions);
            this.setupOnQuit(player, actions);
            this.setupOnDeath(player, actions);
        } else {
            this.plugin.getMyLogger().warning("Failed to load section \"player\" from file \"config.yml\". Please check your configuration file, or delete it and restart your server!");
            this.plugin.getMyLogger().warning("If you think this is a plugin error, leave a issue on the https://github.com/grounbreakingmc/VoidFall/issues");
        }
    }

    private void setupOnJoin(final ConfigurationSection playerSection, final Actions actions) {
        final ConfigurationSection joinSection = playerSection.getConfigurationSection("on-server-join");
        final JoinListener joinListener = this.plugin.getJoinListener();
        if (joinSection != null) {
            this.isPlayerServerJoinTriggerRandom = joinSection.getBoolean("random");
            final List<String> joinCommands = joinSection.getStringList("execute-commands");
            if (!joinCommands.isEmpty()) {
                for (int i = 0; i < joinCommands.size(); i++) {
                    final String command = joinCommands.get(i);
                    final AbstractAction action = actions.getAction(command, null);
                    this.playerServerJoinActions.add(action);
                }
                RegisterUtil.register(this.plugin, joinListener);
                return;
            }
        }

        RegisterUtil.unregister(joinListener);
    }

    private void setupOnQuit(final ConfigurationSection playerSection, final Actions actions) {
        final ConfigurationSection quitSection = playerSection.getConfigurationSection("on-server-leave");
        final QuitListener quitListener = this.plugin.getQuitListener();
        if (quitSection != null) {
            this.isPlayerServerQuitTriggerRandom = quitSection.getBoolean("random");
            final List<String> quitCommands = quitSection.getStringList("execute-commands");
            if (!quitCommands.isEmpty()) {
                for (int i = 0; i < quitCommands.size(); i++) {
                    final String command = quitCommands.get(i);
                    final AbstractAction action = actions.getAction(command, null);
                    this.playerServerQuitActions.add(action);
                }
                RegisterUtil.register(this.plugin, quitListener);
                return;
            }
        }

        RegisterUtil.unregister(quitListener);
    }

    private void setupOnDeath(final ConfigurationSection playerSection, final Actions actions) {
        final ConfigurationSection deathSection = playerSection.getConfigurationSection("on-death");
        final DeathListener deathListener = this.plugin.getDeathListener();
        if (deathSection != null) {
            this.isPlayerDeathTriggerRandom = deathSection.getBoolean("random");
            this.isInstantlyRespawnEnabled = deathSection.getBoolean("instantly-respawn");
            final List<String> deathCommands = deathSection.getStringList("execute-commands");
            if (!deathCommands.isEmpty()) {
                for (int i = 0; i < deathCommands.size(); i++) {
                    final String command = deathCommands.get(i);
                    final AbstractAction action = actions.getAction(command, null);
                    this.playerDeathActions.add(action);
                }
                RegisterUtil.register(this.plugin, deathListener);
                return;
            }
        }

        RegisterUtil.unregister(deathListener);
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
