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

import com.google.common.collect.Sets;
import com.sk89q.worldedit.event.platform.CommandEvent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public final class WESelectionListener implements Listener {

    private final WorldEditSUIPlugin plugin;
    private final Set<String> weCommands;
    private Material wand;

    public WESelectionListener(final WorldEditSUIPlugin plugin) {
        this.plugin = plugin;
        plugin.getWorldEditPlugin().getWorldEdit().getEventBus().register(this);
        weCommands = Sets.newHashSet("pos1", "pos2", "chunk", "hpos1", "hpos2", "expand", "contract", "shift", "outset", "inset",
                "copy", "cut", "rotate", "flip", "clearclipboard");

        try {
            wand = plugin.getRegionHelper().getWand(plugin.getWorldEditPlugin());
        } catch (final Exception e) {
            // Fallback if the item cannot be read for some reason
            if (plugin.getSettings().getWandItem().isEmpty()) {
                plugin.getLogger().info("If you have set another item than the wooden axe as the WE wand, please write it into the WESUI config, for example:");
                plugin.getLogger().info("wand: WOODEN_SWORD");
                wand = Material.getMaterial("WOODEN_AXE");
                return;
            }

            final Material material = Material.getMaterial(plugin.getSettings().getWandItem());
            if (material == null) {
                plugin.getLogger().info("Unknown Material: " + plugin.getSettings().getWandItem());
                wand = Material.getMaterial("WOODEN_AXE");
            } else
                wand = material;
        }
    }

    @EventHandler
    public void playerInteract(final PlayerInteractEvent event) {
        if (!plugin.getSettings().isExpiryEnabled()) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final Player player = event.getPlayer();
        final ItemStack item = player.getItemInHand();
        if (item == null) return;
        if (wand != null) {
            if (wand != item.getType()) return;
        } else {
            final String type = item.getType().name();
            if (!type.endsWith("_AXE") || !type.contains("WOOD")) return;
        }

        plugin.getUserManager().getUser(player).setExpireTimestamp(System.currentTimeMillis() + plugin.getSettings().getExpiresAfterMillis());
    }

    @Subscribe
    public void worldEditCommand(final CommandEvent event) {
        if (!plugin.getSettings().isExpiryEnabled()) return;

        String arguments = event.getArguments();
        while (!arguments.isEmpty() && arguments.charAt(0) == '/') {
            arguments = arguments.substring(1);
        }

        if (arguments.isEmpty()) return;

        final String command = arguments.split(" ", 2)[0].toLowerCase(Locale.ROOT);
        if (weCommands.stream().noneMatch(command::equals)) return;

        final UUID uuid = event.getActor().getUniqueId();
        plugin.getUserManager().getUser(uuid).setExpireTimestamp(System.currentTimeMillis() + plugin.getSettings().getExpiresAfterMillis());
    }
}
