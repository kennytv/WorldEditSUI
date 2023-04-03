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

package eu.kennytv.worldeditsui.listener;

import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

        plugin.runAsync(() -> {
            if (!plugin.updateAvailable()) return;
            if (!player.isOnline()) return;

            player.sendMessage(plugin.getPrefix() + "§cThere is a newer version available: §aVersion " + plugin.getNewestVersion() + "§c, you're on §a" + plugin.getDescription().getVersion());
            notified.add(player.getUniqueId());

            try {
                final TextComponent tc1 = new TextComponent(TextComponent.fromLegacyText(plugin.getPrefix()));
                final TextComponent tc2 = new TextComponent(TextComponent.fromLegacyText("§cDownload it at: §6https://www.spigotmc.org/resources/worldeditsui.60726/"));
                final TextComponent click = new TextComponent(TextComponent.fromLegacyText(" §7§l§o(CLICK ME)"));
                click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/worldeditsui.60726/"));
                click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aDownload the latest version").create()));
                tc1.addExtra(tc2);
                tc1.addExtra(click);

                player.spigot().sendMessage(tc1);
            } catch (final Exception e) {
                player.sendMessage(plugin.getPrefix() + "§cDownload it at: §6https://www.spigotmc.org/resources/worldeditsui.60726/");
            }
        });
    }
}
