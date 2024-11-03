package noslowdwn.voidfall.handlers;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.config.ConfigValues;
import noslowdwn.voidfall.utils.colorizer.IColorizer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.Bukkit.getServer;

public class Actions {

    private final VoidFall plugin;
    private final ConfigValues configValues;
    private final IColorizer colorizer;

    public Actions(final VoidFall plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
        this.colorizer = plugin.getColorizer();
    }

    public void executeRandom(Player p, List<String> list, String world, String cause) {
        Random random = new Random();
        String cmd = list.get(random.nextInt(list.size()));
        execute(p, cmd, world, cause);
    }

    public void execute(Player p, String str, String world, String cause) {
        str = str
                .replace("%player%", p.getName())
                .replace("%world%", world)
                .replace("%world_display_name%", this.configValues.getWorldDisplayName(world));

        if (str.startsWith("[CONSOLE] ")) {
            str = str.replace("[CONSOLE] ", "");
            if (str.isEmpty()) {
                this.plugin.debug("Missing command for [CONSOLE] action. Check your config file.", p, "warn");
                this.plugin.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            } else {
                final String fStr = str;
                getScheduler().runTask(this.plugin, () ->
                {
                    this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), fStr);
                });
            }
        } else if (str.startsWith("[MESSAGE] ")) {
            str = str.replace("[MESSAGE] ", "").replace("[MESSAGE]", "");
            p.sendMessage(colorizer.colorize(str));
        } else if (str.startsWith("[BROADCAST] ")) {
            str = str.replace("[BROADCAST] ", "").replace("[BROADCAST]", "");
            String finalStr = str;
            Bukkit.getOnlinePlayers().stream()
                    .filter(Objects::nonNull)
                    .forEach(
                            player -> {
                                player.sendMessage(colorizer.colorize(finalStr));
                            });
        } else if (str.startsWith("[PLAYER] ")) {
            str = str.replace("[PLAYER] ", "");
            if (str.isEmpty()) {
                this.plugin.debug("Missing command for [PLAYER] action. Check your config file.", p, "warn");
                this.plugin.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            } else {
                final String fStr = str;
                getScheduler().runTask(this.plugin, () ->
                {
                    p.chat("/" + fStr);
                });
            }
        } else if (str.startsWith("[TITLE] ")) {
            str = str.replace("[TITLE] ", "");

            String[] params = str.split(";", 5);

            String main = "", sub = "";
            int fadeIn = 10, stay = 40, fadeOut = 15;
            switch (params.length) {
                case 0:
                    this.plugin.debug("Missing arguments for [TITLE] action. Check your config file.", p, "warn");
                    this.plugin.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
                    break;
                case 5:
                    try {
                        fadeOut = Integer.parseInt(params[4]);
                    } catch (NumberFormatException e) {
                        this.plugin.debug("The value: " + params[4] + " specified in \"FadeOut\" for the [TITLE] action is invalid. Please check your config file.", p, "warn");
                    }
                case 4:
                    try {
                        stay = Integer.parseInt(params[3]);
                    } catch (NumberFormatException e) {
                        this.plugin.debug("The value: " + params[3] + " specified in \"Stay\" for the [TITLE] action is invalid. Please check your config file.", p, "warn");
                    }
                case 3:
                    try {
                        stay = Integer.parseInt(params[2]);
                    } catch (NumberFormatException e) {
                        this.plugin.debug("The value: " + params[2] + " specified in \"FadeIn\" for the [TITLE] action is invalid. Please check your config file.", p, "warn");
                    }
                case 2:
                    sub = params[1];
                case 1:
                    main = params[0];
                default:
                    final String text = main, subText = sub;
                    final int stayTime = stay, fadeOutTime = fadeOut;
                    getScheduler().runTask(this.plugin, () ->
                    {
                        p.sendTitle(colorizer.colorize(text), colorizer.colorize(subText), fadeIn, stayTime, fadeOutTime);
                    });
            }
        } else if (str.startsWith("[ACTIONBAR] ")) {
            str = str.replace("[ACTIONBAR] ", "");
            if (str.isEmpty()) {
                this.plugin.debug("Missing arguments for [ACTIONBAR] action. Check your config file.", p, "warn");
                this.plugin.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            } else {
                final String fStr = str;
                getScheduler().runTask(this.plugin, () ->
                {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colorizer.colorize(fStr)));
                });
            }
            // Sound test = Sound.BLOCK_NOTE_XYLOPHONE;
        } else if (str.startsWith("[PLAY_SOUND] ") || str.startsWith("[PLAY_SOUND_ALL] ")) {
            boolean all = str.startsWith("[PLAY_SOUND_ALL] ");
            String[] params = str
                    .replace("[PLAY_SOUND] ", "")
                    .replace("[PLAY_SOUND_ALL] ", "")
                    .split(";", 3);

            Sound sound = Sound.ENTITY_SHULKER_HURT_CLOSED;
            float volume = 1f, pitch = 1f;

            switch (params.length) {
                case 0:
                    if (all) {
                        this.plugin.debug("Missing arguments for [PLAY_SOUND_ALL] action! Check your config file.", p, "warn");
                        this.plugin.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
                    } else {
                        this.plugin.debug("Missing arguments for [PLAY_SOUND] action! Check your config file.", p, "warn");
                        this.plugin.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
                    }
                    break;
                case 3:
                    try {
                        pitch = Float.parseFloat(params[2]);
                    } catch (final NumberFormatException exception) {
                        if (all) {
                            this.plugin.debug("The value: " + params[2] + " specified in \"Pitch\" for the [PLAY_SOUND_ALL] action is invalid. Please check your config file.", p, "warn");
                        } else {
                            this.plugin.debug("The value: " + params[2] + " specified in \"Pitch\" for the [PLAY_SOUND] action is invalid. Please check your config file.", p, "warn");
                        }
                    }
                case 2:
                    try {
                        volume = Float.parseFloat(params[1]);
                    } catch (final NumberFormatException exception) {
                        if (all) {
                            this.plugin.debug("The value: " + params[1] + " specified in \"Volume\" for the [PLAY_SOUND_ALL] action is invalid. Please check your config file.", p, "warn");
                        } else {
                            this.plugin.debug("The value: " + params[1] + " specified in \"Volume\" for the [PLAY_SOUND] action is invalid. Please check your config file.", p, "warn");
                        }
                    }
                case 1:
                    try {
                        sound = Sound.valueOf(params[0].toUpperCase());
                    } catch (final IllegalArgumentException exception) {
                        if (all) {
                            this.plugin.debug("The value: " + params[0] + " specified in \"Sound\" for the [PLAY_SOUND_ALL] action is invalid. Please check your config file.", p, "warn");
                        } else {
                            this.plugin.debug("The value: " + params[0] + " specified in \"Sound\" for the [PLAY_SOUND] action is invalid. Please check your config file.", p, "warn");
                        }
                    }
                default:
                    final Sound fSound = sound;
                    float fVolume = volume, fPitch = pitch;
                    getScheduler().runTask(this.plugin, () ->
                    {
                        if (all) {
                            Bukkit.getOnlinePlayers().stream()
                                    .filter(Objects::nonNull)
                                    .forEach(player ->
                                    {
                                        p.playSound(p.getLocation(), fSound, fVolume, fPitch);
                                    });
                        } else {
                            p.playSound(p.getLocation(), fSound, fVolume, fPitch);
                        }
                    });
            }
        } else if (str.startsWith("[EFFECT] ")) {
            String[] params = str.replace("[EFFECT] ", "").split(";", 3);

            PotionEffectType effect = PotionEffectType.BLINDNESS;
            int duration = 60, amplifier = 1;

            switch (params.length) {
                case 0:
                    this.plugin.debug("Missing arguments for [EFFECT] action! Check your config file.", p, "warn");
                    this.plugin.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
                    break;
                case 3:
                    try {
                        duration = Integer.parseInt(params[2]) * 20;
                    } catch (final NumberFormatException e) {
                        e.printStackTrace();
                        this.plugin.debug("The value: " + params[2] + " specified in \"Duration\" for the [EFFECT] action is invalid. Please check your config file.", p, "warn");
                    }
                case 2:
                    try {
                        amplifier = Integer.parseInt(params[2]);
                    } catch (final NumberFormatException e) {
                        e.printStackTrace();
                        this.plugin.debug("The value: " + params[1] + " specified in \"Amplifier\" for the [EFFECT] action is invalid. Please check your config file.", p, "warn");
                    }
                case 1:
                    effect = PotionEffectType.getByName(params[0].toUpperCase());
                    if (effect == null) {
                        this.plugin.debug("The value: " + params[0] + " specified in \"PotionEffect\" for the [EFFECT] action is invalid. Please check your config file.", p, "warn");
                    }
                default:
                    final PotionEffectType fEffect = effect;
                    int fDuration = duration, fAmplifier = amplifier;
                    getScheduler().runTask(this.plugin, () ->
                    {
                        p.addPotionEffect(new PotionEffect(fEffect, fDuration, fAmplifier));
                    });
            }
        } else if (str.startsWith("[TELEPORT] ")) {
            String[] params = str.replace("[TELEPORT] ", "").split(";", 6);

            World tpWorld = null;
            double x = 0, y = 90, z = 0;
            float yaw = 180f, pitch = 0f;

            switch (params.length) {
                case 0:
                    this.plugin.debug("Missing arguments for [TELEPORT] action! Check your config file.", p, "warn");
                    this.plugin.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
                    break;
                case 6:
                    try {
                        if (params[5].charAt(0) == '~') {
                            float arg = Float.parseFloat(params[5].replace("~", ""));
                            yaw = p.getLocation().getYaw() + arg;
                        } else {
                            yaw = Float.parseFloat(params[5]);
                        }
                    } catch (final NumberFormatException e) {
                        this.plugin.debug("The value: " + params[5] + " specified in \"Yaw\" for the [TELEPORT] action is invalid. Please check your config file.", p, "warn");
                    }
                case 5:
                    try {
                        if (params[4].charAt(0) == '~') {
                            float arg = Float.parseFloat(params[4].replace("~", ""));
                            pitch = p.getLocation().getPitch() + arg;
                        } else {
                            pitch = Float.parseFloat(params[4]);
                        }
                    } catch (final NumberFormatException e) {
                        this.plugin.debug("The value: " + params[4] + " specified in \"Pitch\" for the [TELEPORT] action is invalid. Please check your config file.", p, "warn");
                    }
                case 4:
                    try {
                        if (params[3].charAt(0) == '~') {
                            float arg = Float.parseFloat(params[3].replace("~", ""));
                            z = p.getLocation().getZ() + arg;
                        } else {
                            z = Double.parseDouble(params[3]);
                        }
                    } catch (final NumberFormatException e) {
                        this.plugin.debug("The value: " + params[3] + " specified in \"Cord z\" for the [TELEPORT] action is invalid. Please check your config file.", p, "warn");
                    }
                case 3:
                    try {
                        if (params[2].charAt(0) == '~') {
                            float arg = Float.parseFloat(params[2].replace("~", ""));
                            z = p.getLocation().getZ() + arg;
                        } else {
                            y = Double.parseDouble(params[2]);
                        }
                    } catch (final NumberFormatException e) {
                        this.plugin.debug("The value: " + params[2] + " specified in \"Cord y\" for the [TELEPORT] action is invalid. Please check your config file.", p, "warn");
                    }
                case 2:
                    try {
                        if (params[1].charAt(0) == '~') {
                            float arg = Float.parseFloat(params[1].replace("~", ""));
                            x = p.getLocation().getX() + arg;
                        } else {
                            x = Double.parseDouble(params[1]);
                        }
                    } catch (final NumberFormatException e) {
                        this.plugin.debug("The value: " + params[1] + " specified in \"Cord x\" for the [TELEPORT] action is invalid. Please check your config file.", p, "warn");
                    }
                case 1:
                    if (params[0].equals("~")) {
                        tpWorld = p.getLocation().getWorld();
                    } else {
                        tpWorld = getServer().getWorld(params[0]);
                    }
                default:
                    if (tpWorld == null) {
                        this.plugin.debug("The value: " + params[0] + " specified in \"World\" for the [TELEPORT] action is invalid. Please check your config file.", p, "warn");
                        tpWorld = getServer().getWorlds().get(0);
                    }

                    Location location = tpWorld.getSpawnLocation();
                    location.setX(x);
                    location.setY(y);
                    location.setZ(z);
                    location.setPitch(pitch);
                    location.setYaw(yaw);
                    getScheduler().runTask(this.plugin, () ->
                    {
                        p.teleport(location);
                    });
            }
        } else if (str.startsWith("[GAMEMODE] ")) {
            str = str.replace("[GAMEMODE] ", "");
            GameMode gm;
            if (str.isEmpty()) {
                this.plugin.debug("Missing arguments for [GAMEMODE] action! Check your config file.", p, "warn");
                this.plugin.debug("Path to: worlds." + world + ".execute-commands", p, "warn");
            } else {
                switch (str) {
                    case "1":
                    case "creative":
                        gm = GameMode.CREATIVE;
                        break;
                    case "2":
                    case "adventure":
                        gm = GameMode.ADVENTURE;
                        break;
                    case "3":
                    case "spectator":
                        gm = GameMode.SPECTATOR;
                        break;
                    case "0":
                    case "survival":
                        gm = GameMode.SURVIVAL;
                        break;
                    default:
                        this.plugin.debug("The gamemode given for [GAMEMODE]: " + str + ", doesn't exist or not valid!", p, "warn");
                        this.plugin.debug("&cPath to: worlds." + world + ".execute-commands", p, "warn");
                        return;
                }

                getScheduler().runTask(this.plugin, () ->
                {
                    p.setGameMode(gm);
                });
            }
        } else {
            this.plugin.debug("&cYou're trying to cause an action that doesn't exist.", p, "warn");
            this.plugin.debug("&cPath to: worlds." + world + ".execute-commands", p, "warn");
            this.plugin.debug("&cAction: " + str, p, "warn");
        }
    }
}