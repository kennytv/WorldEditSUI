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
        if (!(sender instanceof Player)) return true;

        final Player player = (Player) sender;
        if (checkPermission(player, "command")) return true;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                if (checkPermission(player, "command.toggle")) return true;
                final User user = plugin.getUserManager().getUser(player);
                if (!user.isSelectionShown()) {
                    player.sendMessage(plugin.getPrefix() + "§aParticles of your selection are now shown again!");
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cParticles of your selection are now hidden!");
                }

                user.setSelectionShown(!user.isSelectionShown());
                if (plugin.getSettings().persistentToggles()) {
                    plugin.getSettings().setUserData("selection." + player.getUniqueId(), user.isSelectionShown());
                }
            } else if (args[0].equalsIgnoreCase("toggleclipboard")) {
                if (checkPermission(player, "command.toggleclipboard")) return true;
                final User user = plugin.getUserManager().getUser(player);
                if (!user.isClipboardShown()) {
                    player.sendMessage(plugin.getPrefix() + "§aParticles of your clipboard are now shown at relative to your location!");
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cParticles of your clipboard are now hidden again!");
                }

                user.setClipboardShown(!user.isClipboardShown());
                if (plugin.getSettings().persistentToggles()) {
                    plugin.getSettings().setUserData("clipboard." + player.getUniqueId(), user.isClipboardShown());
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (checkPermission(player, "command.reload")) return true;
                plugin.getSettings().loadSettings();
                player.sendMessage(plugin.getPrefix() + "§aReloaded config file!");
            } else
                sendHelp(player);
        } else
            sendHelp(player);
        return true;
    }

    private void sendHelp(final Player player) {
        player.sendMessage("");
        player.sendMessage("§8===========[ §eWorldEditCUI §8| §eVersion: §e" + plugin.getVersion() + " §8]===========");
        if (player.hasPermission("wecui.command.reload"))
            player.sendMessage("§6/wecui reload §7(Reloads the config file)");
        if (player.hasPermission("wecui.command.toggle"))
            player.sendMessage("§6/wecui toggle §7(Toggles the visibility of your selection-particles)");
        if (player.hasPermission("wecui.command.toggleclipboard"))
            player.sendMessage("§6/wecui toggleclipboard §7(Toggles the visibility of your clipboard-particles)");
        player.sendMessage("§8× §eVersion " + plugin.getVersion() + " §7by §bKennyTV");
        player.sendMessage("§8===========[ §eWorldEditCUI §8| §eVersion: §e" + plugin.getVersion() + " §8]===========");
        player.sendMessage("");
    }

    private boolean checkPermission(final Player player, final String permission) {
        if (!player.hasPermission("wecui." + permission)) {
            player.sendMessage(plugin.getPrefix() + "§cYou don't have the permission to use this command.");
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
}
