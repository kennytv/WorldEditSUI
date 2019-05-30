/*
 * WorldEditCUI - https://git.io/wecui
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

package eu.kennytv.worldeditcui.command;

import eu.kennytv.worldeditcui.WorldEditCUIPlugin;
import eu.kennytv.worldeditcui.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WECUICommand implements CommandExecutor, TabCompleter {
    private final WorldEditCUIPlugin plugin;

    public WECUICommand(final WorldEditCUIPlugin plugin) {
        this.plugin = plugin;
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
                player.sendMessage(getMessage(user.isSelectionShown() ? "particlesHidden" : "particlesShown"));
                user.setSelectionShown(!user.isSelectionShown());
                if (plugin.getSettings().hasPersistentToggles()) {
                    plugin.getSettings().setUserData("selection." + player.getUniqueId(), user.isSelectionShown());
                }
            } else if (args[0].equalsIgnoreCase("toggleclipboard")) {
                if (!(sender instanceof Player)) return true;
                if (checkPermission(sender, "command.toggleclipboard")) return true;

                final Player player = (Player) sender;
                final User user = plugin.getUserManager().getUser(player);
                if (!user.isClipboardShown()) {
                    player.sendMessage(getMessage("clipboardShown"));
                } else {
                    player.sendMessage(getMessage("clipboardHidden"));
                }

                user.setClipboardShown(!user.isClipboardShown());
                if (plugin.getSettings().hasPersistentToggles()) {
                    plugin.getSettings().setUserData("clipboard." + player.getUniqueId(), user.isClipboardShown());
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (checkPermission(sender, "command.reload")) return true;

                plugin.getSettings().loadSettings();
                plugin.getSettings().loadLanguageFile();
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
        sender.sendMessage("§8===========[ §eWorldEditCUI §8]===========");
        if (sender.hasPermission("wecui.command.reload"))
            sender.sendMessage(getMessage("helpReload"));
        if (sender.hasPermission("wecui.command.toggle"))
            sender.sendMessage(getMessage("helpToggle"));
        if (sender.hasPermission("wecui.command.toggleclipboard"))
            sender.sendMessage(getMessage("helpToggleClipboard"));
        sender.sendMessage("§8× §eVersion " + plugin.getVersion() + " §7by §bKennyTV");
        sender.sendMessage("§8===========[ §eWorldEditCUI §8]===========");
        sender.sendMessage("");
    }

    private boolean checkPermission(final CommandSender sender, final String permission) {
        if (!sender.hasPermission("wecui." + permission)) {
            sender.sendMessage(getMessage("noPermission"));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String s, final String[] args) {
        if (args.length != 1 || !sender.hasPermission("wecui.command")) return Collections.emptyList();

        final String arg = args[0].toLowerCase();
        final List<String> list = new ArrayList<>();
        checkString(sender, arg, "reload", list);
        checkString(sender, arg, "toggle", list);
        checkString(sender, arg, "toggleclipboard", list);
        return list;
    }

    private void checkString(final CommandSender sender, final String s, final String command, final List<String> list) {
        if (!s.isEmpty() && !command.startsWith(s)) return;
        if (sender.hasPermission("wecui.command." + command))
            list.add(command);
    }

    private String getMessage(final String path) {
        return plugin.getSettings().getMessage(path);
    }
}
