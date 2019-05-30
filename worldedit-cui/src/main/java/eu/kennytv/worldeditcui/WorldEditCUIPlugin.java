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

package eu.kennytv.worldeditcui;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import eu.kennytv.worldeditcui.command.WECUICommand;
import eu.kennytv.worldeditcui.compat.IRegionHelper;
import eu.kennytv.worldeditcui.compat.SimpleVector;
import eu.kennytv.worldeditcui.drawer.DrawManager;
import eu.kennytv.worldeditcui.drawer.base.Drawer;
import eu.kennytv.worldeditcui.listener.PlayerJoinListener;
import eu.kennytv.worldeditcui.listener.PlayerQuitListener;
import eu.kennytv.worldeditcui.listener.WESelectionListener;
import eu.kennytv.worldeditcui.metrics.MetricsLite;
import eu.kennytv.worldeditcui.user.User;
import eu.kennytv.worldeditcui.user.UserManager;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author KennyTV
 */
public final class WorldEditCUIPlugin extends JavaPlugin {
    private static final String PREFIX = "§8[§eWorldEditCUI§8] ";
    private IRegionHelper regionHelper;
    private UserManager userManager;
    private Settings settings;
    private DrawManager drawManager;
    private WorldEditPlugin worldEditPlugin;
    private String version;
    private int expiryTask = -1;
    private int persistentTogglesTask = -1;

    @Override
    public void onEnable() {
        version = getDescription().getVersion();
        getLogger().info("Plugin by KennyTV");

        settings = new Settings(this);
        userManager = new UserManager(settings);
        drawManager = new DrawManager(this);

        final PluginManager pm = getServer().getPluginManager();
        worldEditPlugin = ((WorldEditPlugin) pm.getPlugin("WorldEdit"));

        try {
            Class.forName("com.sk89q.worldedit.math.Vector2");
            regionHelper = new eu.kennytv.worldeditcui.compat.we7.RegionHelper();
        } catch (final ClassNotFoundException e) {
            regionHelper = new eu.kennytv.worldeditcui.compat.we6.RegionHelper();
        }

        // Imagine someone using the server reload command 👀
        getServer().getOnlinePlayers().forEach(p -> userManager.createUser(p));

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(userManager), this);
        pm.registerEvents(new WESelectionListener(this), this);
        getCommand("worldeditcui").setExecutor(new WECUICommand(this));

        // Start tasks
        getServer().getScheduler().runTaskTimerAsynchronously(this, this::updateSelections, settings.getParticleSendIntervall(), settings.getParticleSendIntervall());
        checkTasks();

        new MetricsLite(this);
    }

    @Override
    public void onDisable() {
        settings.saveData();
    }

    public void checkTasks() {
        // Start tasks if not already running, or cancel them if one is running but now disabled in the config
        if (expiryTask == -1 && settings.isExpiryEnabled()) {
            expiryTask = getServer().getScheduler().runTaskTimer(this, () -> {
                userManager.getExpireTimestamps().entrySet().removeIf(entry -> {
                    final boolean remove = entry.getValue() < System.currentTimeMillis();
                    if (remove && settings.hasExpireMessage()) {
                        getServer().getPlayer(entry.getKey()).sendMessage(settings.getMessage("idled"));
                    }
                    return remove;
                });
            }, 20, 20).getTaskId();
        } else if (expiryTask != -1 && !settings.isExpiryEnabled()) {
            getServer().getScheduler().cancelTask(expiryTask);
            expiryTask = -1;
        }

        if (persistentTogglesTask == -1 && settings.hasPersistentToggles()) {
            // Check every 15 minutes
            persistentTogglesTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> settings.saveData(), 18000, 18000).getTaskId();
        } else if (persistentTogglesTask != -1 && !settings.hasPersistentToggles()) {
            getServer().getScheduler().cancelTask(persistentTogglesTask);
            persistentTogglesTask = -1;
        }
    }

    private void updateSelections() {
        getServer().getOnlinePlayers().forEach(player -> {
            if (!player.isOnline()) return;

            final User user = userManager.getUser(player);
            if (user == null) return;
            if (!user.isSelectionShown() && !user.isClipboardShown()) return;
            if (settings.isExpiryEnabled() && !userManager.getExpireTimestamps().containsKey(player.getUniqueId()))
                return;
            if (settings.getPermission() != null && !player.hasPermission(settings.getPermission())) return;

            final LocalSession session = worldEditPlugin.getSession(player);
            final RegionSelector selector = session.getRegionSelector(new BukkitWorld(player.getWorld()));

            // Clipboard
            if (user.isClipboardShown()) {
                try {
                    final Clipboard clipboard = session.getClipboard().getClipboard();
                    final SimpleVector origin = regionHelper.getOrigin(clipboard);
                    final Location location = player.getLocation();
                    final Region region = clipboard.getRegion();
                    final Region shiftedRegion = regionHelper.shift(region, location.getBlockX() - origin.getX(), location.getBlockY() - origin.getY(), location.getBlockZ() - origin.getZ());
                    drawManager.getDrawer("cuboid").draw(settings.sendParticlesToAll() ? null : player, shiftedRegion, true);
                } catch (final EmptyClipboardException ignored) {
                    // Ignore if there's no clipboard
                }
            }

            // Selection
            if (user.isSelectionShown()) {
                final Region region;
                try {
                    region = selector.getRegion();
                } catch (final IncompleteRegionException ignored) {
                    return;
                }
                final Drawer drawer = drawManager.getDrawer(selector.getTypeName());
                if (drawer != null)
                    drawer.draw(settings.sendParticlesToAll() ? null : player, region);
            }
        });
    }

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public Settings getSettings() {
        return settings;
    }

    public String getPrefix() {
        return PREFIX;
    }

    public String getVersion() {
        return version;
    }

    public IRegionHelper getRegionHelper() {
        return regionHelper;
    }
}
