package com.github.groundbreakingmc.voidfall.commands;

import com.github.groundbreakingmc.voidfall.VoidFall;
import com.github.groundbreakingmc.voidfall.utils.PapiUtil;
import com.github.groundbreakingmc.voidfall.utils.UpdatesChecker;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class VoidFallCommand implements CommandExecutor, TabCompleter {

    private final VoidFall plugin;

    public VoidFallCommand(final VoidFall plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            this.usageError(sender);
            return true;
        }

        switch(args[0].toLowerCase()) {
            case "reload":
                this.processReload(sender);
                break;
            case "update":
                this.processUpdate(sender);
                break;
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
        PapiUtil.setPapiStatus(this.plugin);

        final long endTime = System.currentTimeMillis();
        final String resultTime = String.valueOf(endTime - startTime);
        sender.sendMessage(this.plugin.getConfigValues().getReloadMessage().replace("%time%", resultTime));
    }

    private void processUpdate(final CommandSender sender) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§4[NewbieGuard] §cThis command can only be executed only by the console!");
            return;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () ->
                new UpdatesChecker(this.plugin).downloadJar(true)
        );
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
            if ("update".startsWith(input) && sender instanceof ConsoleCommandSender) {
                list.add("update");
            }
            if ("reload".startsWith(input) && sender.hasPermission("voidfall.reload")) {
                list.add("reload");
            }
        }

        return list;
    }
}
