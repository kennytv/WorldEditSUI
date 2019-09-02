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

package eu.kennytv.worldeditsui.listener;

import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerJoinListener implements Listener {

    private final Set<UUID> notified = new HashSet<>();
    private final WorldEditSUIPlugin plugin;

    public PlayerJoinListener(final WorldEditSUIPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        plugin.getUserManager().createUser(player);
        if (!plugin.getSettings().hasUpdateChecks()) return;
        if (!player.hasPermission("worldeditsui.admin")) return;
        if (notified.contains(player.getUniqueId())) return;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!plugin.updateAvailable()) return;
            if (!player.isOnline()) return;

            player.sendMessage(plugin.getPrefix() + "§cThere is a newer version available: §aVersion " + plugin.getNewestVersion() + "§c, you're on §a" + plugin.getDescription().getVersion());
            notified.add(player.getUniqueId());

            player.sendMessage(plugin.getPrefix() + "§cDownload it at: §6https://www.spigotmc.org/resources/worldeditsui.60726/");
        });
    }
}
