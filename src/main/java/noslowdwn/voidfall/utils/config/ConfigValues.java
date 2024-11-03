package noslowdwn.voidfall.utils.config;

import noslowdwn.voidfall.VoidFall;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigValues {

    private static final Map<String, Object> worldDisplayName = new HashMap<>();

    // Player Actions
    private static final List<String> playerActionsList = new ArrayList<>();
    private static boolean isPlayerServerJoinTriggerEnabled, isPlayerServerQuitTriggerEnabled, isPlayerDeathTriggerEnabled;
    private static boolean isInstantlyRespawnEnabled;
    private static boolean isPlayerServerJoinTriggerRandom, isPlayerServerQuitTriggerRandom, isPlayerDeathTriggerRandom;
    private static final List<String> playerServerJoinCommands = new ArrayList<>(), playerServerQuitCommands = new ArrayList<>(), playerDeathCommands = new ArrayList<>();

    // Regions
    private static boolean isRegionsEmpty;
    private static final Map<String, List<String>> enterRegionsWorlds = new HashMap<>(), leaveRegionsWorlds = new HashMap<>();
    private static final List<String> entryRegionsAreUsingRandom = new ArrayList<>(), leaveRegionsAreUsingRandom = new ArrayList<>();
    private static final Map<String, List<String>> entryRegionsCommands = new HashMap<>(), leaveRegionsCommands = new HashMap<>();

    // YCords
    private static final List<String> worldList = new ArrayList<>();
    private static final List<String> worldsWithFloorMode = new ArrayList<>(), worldsWithRoofMode = new ArrayList<>();
    private static final Map<String, Integer> floorWorldsModeHeight = new HashMap<>(), roofWorldsModeHeight = new HashMap<>();
    private static final Map<String, List<String>> floorWorldsCommands = new HashMap<>(), roofWorldsCommands = new HashMap<>();
    private static final List<String> floorWorldsCommandsRandom = new ArrayList<>(), roofWorldsCommandsRandom = new ArrayList<>();
    private static final Map<String, Integer> worldRepeatFix = new HashMap<>();

    private final VoidFall plugin;
    
    public ConfigValues(final VoidFall plugin) {
        this.plugin = plugin;
    }

    public void setupValues() {
        if (!worldDisplayName.isEmpty()) {
            worldDisplayName.clear();
        }
        final FileConfiguration config = new ConfigLoader(plugin).loadAndGet("config", 1.0);
        worldDisplayName.putAll(config.getConfigurationSection("messages.worlds-display-names").getValues(false));

        // Player Actions
        if (!playerActionsList.isEmpty()) {
            playerActionsList.clear();
        }
        playerActionsList.addAll(config.getConfigurationSection("player").getKeys(false));
        isPlayerServerJoinTriggerEnabled = config.contains("player.on-server-join");
        isPlayerServerQuitTriggerEnabled = config.contains("player.on-server-leave");
        isPlayerDeathTriggerEnabled = config.contains("player.on-death");

        isInstantlyRespawnEnabled = config.getBoolean("player.on-death.instantly-respawn", false);

        isPlayerServerJoinTriggerRandom = config.getBoolean("player.on-server-join.random", false);
        isPlayerServerQuitTriggerRandom = config.getBoolean("player.on-server-leave.random", false);
        isPlayerDeathTriggerRandom = config.getBoolean("player.on-death.random", false);

        if (!playerServerJoinCommands.isEmpty()) {
            playerServerJoinCommands.clear();
        }
        playerServerJoinCommands.addAll(config.getStringList("player.on-server-join.execute-commands"));
        if (!playerServerQuitCommands.isEmpty()) {
            playerServerQuitCommands.clear();
        }
        playerServerQuitCommands.addAll(config.getStringList("player.on-server-leave.execute-commands"));
        if (!playerDeathCommands.isEmpty()) {
            playerDeathCommands.clear();
        }
        playerDeathCommands.addAll(config.getStringList("player.on-death.execute-commands"));

        // Regions
        if (!enterRegionsWorlds.isEmpty()) {
            enterRegionsWorlds.clear();
        }
        if (!leaveRegionsWorlds.isEmpty()) {
            leaveRegionsWorlds.clear();
        }
        if (!entryRegionsAreUsingRandom.isEmpty()) {
            entryRegionsAreUsingRandom.clear();
        }
        if (!leaveRegionsAreUsingRandom.isEmpty()) {
            leaveRegionsAreUsingRandom.clear();
        }
        if (!entryRegionsCommands.isEmpty()) {
            entryRegionsCommands.clear();
        }
        if (!leaveRegionsCommands.isEmpty()) {
            leaveRegionsCommands.clear();
        }
        isRegionsEmpty = config.getConfigurationSection("regions").getKeys(false).isEmpty();
        if (!isRegionsEmpty) {
            for (String regionName : config.getConfigurationSection("messages.worlds-display-names").getKeys(false)) {
                if (config.contains("regions." + regionName + ".on-enter")) {
                    enterRegionsWorlds.put(regionName, config.getStringList("regions." + regionName + ".worlds"));
                }
                if (config.contains("regions." + regionName + ".on-leave")) {
                    leaveRegionsWorlds.put(regionName, config.getStringList("regions." + regionName + ".worlds"));
                }
                if (config.getBoolean("regions." + regionName + ".on-enter.random", false)) {
                    entryRegionsAreUsingRandom.add(regionName);
                }
                if (config.getBoolean("regions." + regionName + ".on-leave.random", false)) {
                    leaveRegionsAreUsingRandom.add(regionName);
                }
                entryRegionsCommands.put(regionName, config.getStringList("regions." + regionName + ".on-enter.execute-commands"));
                leaveRegionsCommands.put(regionName, config.getStringList("regions." + regionName + ".on-leave.execute-commands"));
            }
        }

        // YCords
        if (!worldList.isEmpty()) {
            worldList.clear();
        }
        if (!worldsWithFloorMode.isEmpty()) {
            worldsWithFloorMode.clear();
        }
        if (!floorWorldsCommands.isEmpty()) {
            floorWorldsCommands.clear();
        }
        if (!floorWorldsCommandsRandom.isEmpty()) {
            floorWorldsCommandsRandom.clear();
        }
        if (!worldsWithRoofMode.isEmpty()) {
            worldsWithRoofMode.clear();
        }
        if (!roofWorldsCommands.isEmpty()) {
            roofWorldsCommands.clear();
        }
        if (!roofWorldsCommandsRandom.isEmpty()) {
            roofWorldsCommandsRandom.clear();
        }
        if (!worldRepeatFix.isEmpty()) {
            worldRepeatFix.clear();
        }
        if (!floorWorldsModeHeight.isEmpty()) {
            floorWorldsModeHeight.clear();
        }
        if (!roofWorldsModeHeight.isEmpty()) {
            roofWorldsModeHeight.clear();
        }
        worldList.addAll(config.getConfigurationSection("worlds").getKeys(false));
        if (!worldList.isEmpty()) {
            for (String world : config.getConfigurationSection("worlds").getKeys(false)) {
                if (config.contains("worlds." + world + ".floor")) {
                    worldsWithFloorMode.add(world);
                    floorWorldsCommands.put(world, config.getStringList("worlds." + world + ".floor.execute-commands"));
                    if (config.getBoolean("worlds." + world + ".floor.random", false)) {
                        floorWorldsCommandsRandom.add(world);
                    }
                }
                if (config.contains("worlds." + world + ".roof")) {
                    worldsWithRoofMode.add(world);
                    roofWorldsCommands.put(world, config.getStringList("worlds." + world + ".roof.execute-commands"));
                    if (config.getBoolean("worlds." + world + ".roof.random", false)) {
                        roofWorldsCommandsRandom.add(world);
                    }
                }
                worldRepeatFix.put(world, config.getInt("worlds." + world + ".floor.repeat-fix", 3));
            }
            for (String world : worldsWithFloorMode) {
                floorWorldsModeHeight.put(world, config.getInt("worlds." + world + ".floor.executing-height", 0));
            }
            for (String world : worldsWithRoofMode) {
                roofWorldsModeHeight.put(world, config.getInt("worlds." + world + ".roof.executing-height", 666));
            }
        }
    }

    public String getWorldDisplayName(String worldName) {
        return worldDisplayName.getOrDefault(worldName, worldName).toString();
    }

    public boolean containsPlayerActionsList(String action) {
        return !playerActionsList.contains(action);
    }

    public boolean isPlayerServerJoinTriggerEnabled() {
        return isPlayerServerJoinTriggerEnabled;
    }

    public boolean isPlayerServerQuitTriggerEnabled() {
        return isPlayerServerQuitTriggerEnabled;
    }

    public boolean isPlayerDeathTriggerEnabled() {
        return isPlayerDeathTriggerEnabled;
    }

    public boolean isInstantlyRespawnEnabled() {
        return isInstantlyRespawnEnabled;
    }

    public boolean isPlayerServerJoinTriggerRandom() {
        return isPlayerServerJoinTriggerRandom;
    }

    public boolean isPlayerServerQuitTriggerRandom() {
        return isPlayerServerQuitTriggerRandom;
    }

    public boolean isPlayerDeathTriggerRandom() {
        return isPlayerDeathTriggerRandom;
    }

    public List<String> getPlayerServerJoinCommands() {
        return playerServerJoinCommands;
    }

    public List<String> getPlayerServerQuitCommands() {
        return playerServerQuitCommands;
    }

    public List<String> getPlayerDeathCommands() {
        return playerDeathCommands;
    }

    public boolean isRegionsEmpty() {
        return isRegionsEmpty;
    }

    public boolean containsEnterRegionWorld(String region, String world) {
        return enterRegionsWorlds.get(region).contains(world);
    }

    public boolean containsLeaveRegionWorld(String region, String world) {
        return enterRegionsWorlds.get(region).contains(world);
    }

    public boolean entryRegionsAreUsingRandom(String region) {
        return entryRegionsAreUsingRandom.contains(region);
    }

    public boolean leaveRegionsAreUsingRandom(String region) {
        return leaveRegionsAreUsingRandom.contains(region);
    }

    public List<String> getEntryRegionsCommands(String region) {
        return entryRegionsCommands.get(region);
    }

    public List<String> getLeaveRegionsCommands(String region) {
        return leaveRegionsCommands.get(region);
    }

    public boolean containsWorld(String worldName) {
        return worldList.contains(worldName);
    }

    public boolean worldHasFloorMode(String worldName) {
        return worldsWithFloorMode.contains(worldName);
    }

    public boolean worldHasRoofMode(String worldName) {
        return worldsWithRoofMode.contains(worldName);
    }

    public int worldFloorHeight(String worldName) {
        return floorWorldsModeHeight.get(worldName);
    }

    public int worldRoofHeight(String worldName) {
        return roofWorldsModeHeight.get(worldName);
    }

    public List<String> getWorldCommands(String worldName, String mode) {
        if (mode.equals("floor")) {
            return floorWorldsCommands.get(worldName);
        } else {
            return roofWorldsCommands.get(worldName);
        }
    }

    public boolean isWorldRunModeRandom(String worldName, String mode) {
        if (mode.equals("floor")) {
            return floorWorldsCommandsRandom.contains(worldName);
        } else {
            return roofWorldsCommandsRandom.contains(worldName);
        }
    }

    public int getWorldRepeatFix(String worldName) {
        return worldRepeatFix.get(worldName);
    }

    public List<String> getWorldsWithFloorMode() {
        return worldsWithFloorMode;
    }

    public List<String> getWorldsWithRoofMode() {
        return worldsWithRoofMode;
    }
}
