/*
 * WorldEditSUI - https://git.io/wesui
 * Copyright (C) 2018 KennyTV (https://github.com/KennyTV)
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
import eu.kennytv.worldeditsui.user.SelectionCache;
import eu.kennytv.worldeditsui.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WESUICommand implements CommandExecutor, TabCompleter {

    private final WorldEditSUIPlugin plugin;
    private final Settings settings;

    public WESUICommand(final WorldEditSUIPlugin plugin) {
        this.plugin = plugin;
        settings = plugin.getSettings();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args) {
        if (checkPermission(sender, "command")) return true;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                if (!(sender instanceof Player)) return true;
                if (checkPermission(sender, "command.toggle")) return true;

                final Player player = (Player) sender;
                final User user = plugin.getUserManager().getUser(player);
                if (user.isSelectionShown()) {
                    player.sendMessage(getMessage("particlesHidden"));
                    user.setSelectionShown(false);

                    final SelectionCache cache = user.getSelectionCache();
                    if (cache != null) {
                        cache.getVectors().clear();
                        user.setSelectionCache(null);
                    }
                } else {
                    player.sendMessage(getMessage("particlesShown"));
                    user.setSelectionShown(true);
                }

                if (settings.hasPersistentToggles()) {
                    settings.setUserData("selection." + player.getUniqueId(), user.isSelectionShown());
                }
            } else if (args[0].equalsIgnoreCase("toggleclipboard")) {
                if (!(sender instanceof Player)) return true;
                if (checkPermission(sender, "command.toggleclipboard")) return true;

                final Player player = (Player) sender;
                final User user = plugin.getUserManager().getUser(player);
                player.sendMessage(user.isClipboardShown() ? getMessage("clipboardHidden") : getMessage("clipboardShown"));
                user.setClipboardShown(!user.isClipboardShown());
                if (settings.hasPersistentToggles()) {
                    settings.setUserData("clipboard." + player.getUniqueId(), user.isClipboardShown());
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (checkPermission(sender, "command.reload")) return true;

                final boolean cache = settings.cacheLocations();
                settings.loadSettings();
                settings.loadLanguageFile();
                // Empty cache to recalculate positions
                plugin.getUserManager().getUsers().values().forEach(user -> user.setSelectionCache(null));
                plugin.checkTasks();
                sender.sendMessage(getMessage("reload"));
            } else
                sendHelp(sender);
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
        sender.sendMessage("§8× §eVersion " + plugin.getVersion() + " §7by §bKennyTV");
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
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String s, final String[] args) {
        if (args.length != 1 || !sender.hasPermission("wesui.command")) return Collections.emptyList();

        final String arg = args[0].toLowerCase();
        final List<String> list = new ArrayList<>();
        checkString(sender, arg, "reload", list);
        checkString(sender, arg, "toggle", list);
        checkString(sender, arg, "toggleclipboard", list);
        return list;
    }

    private void checkString(final CommandSender sender, final String s, final String command, final List<String> list) {
        if (!s.isEmpty() && !command.startsWith(s)) return;
        if (sender.hasPermission("wesui.command." + command))
            list.add(command);
    }

    private String getMessage(final String path) {
        return settings.getMessage(path);
    }
}
