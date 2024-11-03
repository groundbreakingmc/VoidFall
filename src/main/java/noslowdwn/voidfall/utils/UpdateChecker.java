package noslowdwn.voidfall.utils;

import noslowdwn.voidfall.VoidFall;
import noslowdwn.voidfall.utils.colorizer.IColorizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker implements Listener {

    private static final String VERSION_URL = "https://raw.githubusercontent.com/noslowdwn/VoidFall/master/version";
    private static Boolean new_version = false;
    private static String latestVersion, downloadLink;

    private final VoidFall plugin;
    private final IColorizer colorizer;

    public UpdateChecker(final VoidFall plugin) {
        this.plugin = plugin;
        this.colorizer = plugin.getColorizer();
    }

    public void checkVersion() {
        if (this.plugin.getConfig().getBoolean("check-updates", false)) {
            Bukkit.getConsoleSender().sendMessage(colorizer.colorize("&6[VoidFall] Checking for updates..."));

            try {
                URL url = new URL(VERSION_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                in.close();
                connection.disconnect();

                String[] versionInfo = content.toString().split("->");
                if (versionInfo.length != 2) {
                    Bukkit.getConsoleSender().sendMessage(colorizer.colorize("&6[VoidFall] Got version from server is invalid!"));
                    return;
                }

                latestVersion = versionInfo[0].trim();
                downloadLink = versionInfo[1].trim();

                String[] currVersion = this.plugin.getDescription().getVersion().split("\\.");
                String[] newVersion = latestVersion.split("\\.");
                for (int i = 0; i < newVersion.length; i++) {
                    int currVer = Integer.parseInt(currVersion[i]);
                    int newVer = Integer.parseInt(newVersion[i]);

                    if (currVer < newVer) {
                        new_version = true;
                    }
                }

                if (!new_version) {
                    Bukkit.getConsoleSender().sendMessage(colorizer.colorize("&c[VoidFall] No updates were found!"));
                } else {
                    Bukkit.getConsoleSender().sendMessage(colorizer.colorize("&f============ VoidFall ============"));
                    Bukkit.getConsoleSender().sendMessage(colorizer.colorize("&fCurrent version: &7" + this.plugin.getDescription().getVersion()));
                    Bukkit.getConsoleSender().sendMessage(colorizer.colorize("&fNew version: &a" + latestVersion));
                    Bukkit.getConsoleSender().sendMessage(colorizer.colorize("&fDownload link: &7" + downloadLink));
                    Bukkit.getConsoleSender().sendMessage(colorizer.colorize("&f=================================="));
                }
            } catch (Exception e) {
                this.plugin.getMyLogger().warning(colorizer.colorize("&c[VoidFall] Failed to check for updates: " + e.getMessage()));
            }
        }
    }

    @EventHandler
    private void onJoinNotification(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () ->
        {
            if ((p.isOp() || p.hasPermission("voidfall.updates")) && new_version) {
                p.sendMessage("");
                p.sendMessage(colorizer.colorize("&6[VoidFall] &aWas found an update!"));
                p.sendMessage(colorizer.colorize("&fCurrent version: &7" + this.plugin.getDescription().getVersion()));
                p.sendMessage(colorizer.colorize("&fNew version: &a" + latestVersion));
                p.sendMessage(colorizer.colorize("&fDownload link: &7" + downloadLink));
                p.sendMessage("");
            }
        }, 100L);
    }
}