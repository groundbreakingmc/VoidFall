package noslowdwn.voidfall;

import noslowdwn.voidfall.utils.UpdatesChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VoidFallCommand implements CommandExecutor, TabCompleter {

    private final VoidFall plugin;

    public VoidFallCommand(final VoidFall plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {

            return true;
        }

        switch(args[0].toLowerCase()) {
            case "reload":
                this.processReload(sender);
                break;
            case "update":
                if (sender instanceof ConsoleCommandSender) {
                    this.processUpdate();
                    break;
                }
            default:
                this.usageError(sender);
        }

        return true;
    }

    private void processReload(final CommandSender sender) {
        final long startTime = System.currentTimeMillis();
        if (!sender.hasPermission("voidfall.reload")) {
            sender.sendMessage(this.plugin.getConfigValues().getNoPermissionMessage());
            return;
        }

        this.plugin.getConfigValues().setupValues();
        this.plugin.registerRegionsListener();

        final long endTime = System.currentTimeMillis();
        final String resultTime = String.valueOf(endTime - startTime);
        sender.sendMessage(this.plugin.getConfigValues().getReloadMessage().replace("%time%", resultTime));
    }

    private void processUpdate() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> new UpdatesChecker(this.plugin).downloadJar());
    }

    private void usageError(final CommandSender sender) {
        if (!sender.hasPermission("voidfall.reload") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(this.plugin.getConfigValues().getNoPermissionMessage());
            return;
        }

        sender.sendMessage(this.plugin.getConfigValues().getUsageError());
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        final List<String> list = new ArrayList<>();

        if (args.length == 1) {
            final String input = args[0].toLowerCase();
            if (sender instanceof ConsoleCommandSender && "update".startsWith(input)) {
                list.add("update");
            }
            if (sender.hasPermission("voidfall.reload") && "reload".startsWith(input)) {
                list.add("reload");
            }
        }

        return list;
    }
}
