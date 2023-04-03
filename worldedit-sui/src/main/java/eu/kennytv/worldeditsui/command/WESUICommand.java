/*
 * This file is part of WorldEditSUI - https://git.io/wesui
 * Copyright (C) 2018-2021 kennytv (https://github.com/kennytv)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.kennytv.worldeditsui.command;

import eu.kennytv.worldeditsui.Settings;
import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import eu.kennytv.worldeditsui.compat.ProtectedRegionWrapper;
import eu.kennytv.worldeditsui.drawer.base.DrawedType;
import eu.kennytv.worldeditsui.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class WESUICommand implements CommandExecutor, TabCompleter {

    private final WorldEditSUIPlugin plugin;
    private final Settings settings;

    public WESUICommand(final WorldEditSUIPlugin plugin) {
        this.plugin = plugin;
        settings = plugin.getSettings();
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command cmd, @NotNull final String s, @NotNull final String[] args) {
        if (args.length == 1) {
            final String arg = args[0].toLowerCase();
            switch (arg) {
                case "toggle": {
                    if (!(sender instanceof Player)) return true;
                    if (checkPermission(sender, "command.toggle")) return true;

                    final Player player = (Player) sender;
                    final User user = plugin.getUserManager().getUser(player);
                    if (user.isSelectionShown()) {
                        player.sendMessage(getMessage("particlesHidden"));
                        user.setSelectionShown(false);
                        user.clearCaches();
                    } else {
                        player.sendMessage(getMessage("particlesShown"));
                        user.setSelectionShown(true);
                    }

                    if (settings.hasPersistentToggles()) {
                        settings.setUserData("selection." + player.getUniqueId(), user.isSelectionShown());
                    }
                    break;
                }
                case "toggleclipboard": {
                    if (!(sender instanceof Player)) return true;
                    if (checkPermission(sender, "command.toggleclipboard")) return true;

                    final Player player = (Player) sender;
                    final User user = plugin.getUserManager().getUser(player);
                    player.sendMessage(user.isClipboardShown() ? getMessage("clipboardHidden") : getMessage("clipboardShown"));
                    user.setClipboardShown(!user.isClipboardShown());
                    if (settings.hasPersistentToggles()) {
                        settings.setUserData("clipboard." + player.getUniqueId(), user.isClipboardShown());
                    }
                    break;
                }
                case "reload":
                    if (checkPermission(sender, "command.reload")) return true;

                    settings.loadSettings();
                    settings.loadLanguageFile();
                    // Empty cache to recalculate positions
                    plugin.getUserManager().getUsers().values().forEach(User::clearCaches);
                    plugin.reloadTasks();
                    sender.sendMessage(getMessage("reload"));
                    // Reset Bukkit's no perm message for the command
                    plugin.getCommand("worldeditsui").setPermissionMessage(settings.getMessage("noPermission"));
                    break;
                case "showregion": {
                    if (!(sender instanceof Player)) return true;
                    if (checkPermission(sender, "command.showregion")) return true;

                    final Player player = (Player) sender;
                    final User user = plugin.getUserManager().getUser(player);
                    if (user.getSelectedWGRegion() == null) {
                        sendHelp(sender);
                        return true;
                    }

                    user.setSelectedWGRegion(null);
                    user.clearCache(DrawedType.WG_REGION);
                    user.setExpireTimestamp(System.currentTimeMillis() + plugin.getSettings().getExpiresAfterMillis());
                    sender.sendMessage(getMessage("regionCleared"));
                    break;
                }
                default:
                    sendHelp(sender);
                    break;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("showregion")) {
            if (!(sender instanceof Player)) return true;
            if (checkPermission(sender, "command.showregion")) return true;
            if (!plugin.isWorldGuardEnabled()) {
                sender.sendMessage(getMessage("WGNotEnabled"));
                return true;
            }

            final Player player = (Player) sender;
            final ProtectedRegionWrapper region = plugin.getProtectedRegionHelper().getRegion(player.getWorld(), args[1]);
            if (region == null) {
                sender.sendMessage(getMessage("regionNotFound"));
                return true;
            }

            final User user = plugin.getUserManager().getUser(player);
            user.clearCache(DrawedType.WG_REGION);
            user.setSelectedWGRegion(region);
            player.sendMessage(getMessage("regionDisplayed"));
        } else
            sendHelp(sender);
        return true;
    }

    private void sendHelp(final CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(getMessage("helpHeader"));
        if (sender.hasPermission("wesui.command.reload"))
            sender.sendMessage(getMessage("helpReload"));
        if (sender.hasPermission("wesui.command.toggle"))
            sender.sendMessage(getMessage("helpToggle"));
        if (sender.hasPermission("wesui.command.toggleclipboard"))
            sender.sendMessage(getMessage("helpToggleClipboard"));
        if (plugin.isWorldGuardEnabled() && sender.hasPermission("wesui.command.showregion"))
            sender.sendMessage(getMessage("helpShowRegion"));
        sender.sendMessage("§8× §eVersion " + plugin.getVersion() + " §7by §bkennytv");
        sender.sendMessage(getMessage("helpHeader"));
        sender.sendMessage("");
    }

    private boolean checkPermission(final CommandSender sender, final String permission) {
        if (!sender.hasPermission("wesui." + permission)) {
            sender.sendMessage(getMessage("noPermission"));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command cmd, @NotNull final String s, @NotNull final String[] args) {
        if (!sender.hasPermission("wesui.command")) return Collections.emptyList();
        if (args.length == 1) {
            final String arg = args[0].toLowerCase();
            final List<String> list = new ArrayList<>();
            checkString(sender, arg, "reload", list);
            checkString(sender, arg, "toggle", list);
            checkString(sender, arg, "toggleclipboard", list);
            if (plugin.isWorldGuardEnabled()) {
                checkString(sender, arg, "showregion", list);
            }
            return list;
        } else if (args.length == 2) {
            if (sender instanceof Player && plugin.isWorldGuardEnabled()
                    && args[0].equalsIgnoreCase("showregion") && sender.hasPermission("wesui.showregion")) {
                final Set<String> regionNames = plugin.getProtectedRegionHelper().getRegionNames(((Player) sender).getWorld());
                return StringUtil.copyPartialMatches(args[1], regionNames, new ArrayList<>());
            }
        }
        return Collections.emptyList();
    }

    private void checkString(final CommandSender sender, final String s, final String command, final List<String> list) {
        if (!s.isEmpty() && !command.startsWith(s)) return;
        if (sender.hasPermission("wesui.command." + command)) {
            list.add(command);
        }
    }

    private String getMessage(final String path) {
        return settings.getMessage(path);
    }
}
