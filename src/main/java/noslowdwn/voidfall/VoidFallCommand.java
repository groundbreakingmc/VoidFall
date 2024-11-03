package noslowdwn.voidfall;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class VoidFallCommand implements CommandExecutor, TabCompleter {

    private final VoidFall plugin;

    public VoidFallCommand(final VoidFall plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final long startTime = System.currentTimeMillis();
        if (!sender.hasPermission("voidfall.reload")) {
            sender.sendMessage(this.plugin.getConfigValues().getNoPermissionMessage());
            return true;
        }

        this.plugin.getConfigValues().setupValues();

        final boolean isWgEventsEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuardEvents");
        if (!isWgEventsEnabled) {
            this.plugin.getMyLogger().info("Actions on region enter/leave will be disabled!");
            this.plugin.getMyLogger().info("Please download WorldGuardEvents to enable them.");
            this.plugin.getMyLogger().info("https://www.spigotmc.org/resources/worldguard-events.65176/");
        }

        final long endTime = System.currentTimeMillis();
        final String resultTime = String.valueOf(endTime - startTime);
        sender.sendMessage(this.plugin.getConfigValues().getReloadMessage().replace("%time%", resultTime));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
