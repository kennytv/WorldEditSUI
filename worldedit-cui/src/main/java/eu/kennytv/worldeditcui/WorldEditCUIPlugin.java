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
import eu.kennytv.util.particlelib.ParticleEffectUtil;
import eu.kennytv.worldeditcui.command.WECUICommand;
import eu.kennytv.worldeditcui.compat.IRegionHelper;
import eu.kennytv.worldeditcui.compat.SimpleVector;
import eu.kennytv.worldeditcui.drawer.DrawManager;
import eu.kennytv.worldeditcui.listener.PlayerJoinListener;
import eu.kennytv.worldeditcui.listener.PlayerQuitListener;
import eu.kennytv.worldeditcui.listener.WESelectionListener;
import eu.kennytv.worldeditcui.metrics.MetricsLite;
import eu.kennytv.worldeditcui.user.SelectionCache;
import eu.kennytv.worldeditcui.user.User;
import eu.kennytv.worldeditcui.user.UserManager;
import eu.kennytv.worldeditcui.util.SelectionType;
import eu.kennytv.worldeditcui.util.Version;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private Version version;
    private Version newestVersion;
    private int expiryTask = -1;
    private int persistentTogglesTask = -1;

    @Override
    public void onEnable() {
        version = new Version(getDescription().getVersion());
        printEnableMessage();

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
        getServer().getScheduler().runTaskTimerAsynchronously(this, this::updateSelections, settings.getParticleSendInterval(), settings.getParticleSendInterval());
        checkTasks();

        new MetricsLite(this);
    }

    @Override
    public void onDisable() {
        settings.saveData();
    }

    private void printEnableMessage() {
        getLogger().info("Plugin by KennyTV");
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            updateAvailable();
            final int compare = version.compareTo(newestVersion);
            if (compare == -1) {
                getLogger().info("§cNewest version available: §aVersion " + newestVersion + "§c, you're on §a" + version);
            } else if (compare == 1) {
                if (version.getTag().equalsIgnoreCase("snapshot")) {
                    getLogger().info("§cYou're running a development version, please report bugs on the Discord server (https://kennytv.eu/discord).");
                } else {
                    getLogger().info("§cYou're running a version, that doesn't exist! §cN§ai§dc§ee§5!");
                }
            }
        });
    }

    public boolean updateAvailable() {
        try {
            final HttpURLConnection c = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=60726").openConnection();
            final String newVersionString = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z -]", "");
            final Version newVersion = new Version(newVersionString);
            if (newVersion.equals(version)) return false;

            newestVersion = newVersion;
            return version.compareTo(newVersion) == -1;
        } catch (final Exception ignored) {
            return false;
        }
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
        for (final Player player : getServer().getOnlinePlayers()) {
            if (!player.isOnline()) continue;

            final User user = userManager.getUser(player);
            if (user == null) continue;
            if (!user.isSelectionShown() && !user.isClipboardShown()) continue;
            if (settings.isExpiryEnabled() && !userManager.getExpireTimestamps().containsKey(player.getUniqueId()))
                continue;
            if (settings.getPermission() != null && !player.hasPermission(settings.getPermission())) continue;

            final LocalSession session = worldEditPlugin.getSession(player);
            final RegionSelector selector = session.getRegionSelector(new BukkitWorld(player.getWorld()));

            // Clipboard
            if (user.isClipboardShown()) {
                try {
                    final Clipboard clipboard = session.getClipboard().getClipboard();
                    final Region region = clipboard.getRegion();
                    final Location location = player.getLocation();
                    final SimpleVector origin = regionHelper.getOrigin(clipboard);
                    final Region shiftedRegion = regionHelper.shift(region,
                            location.getBlockX() - origin.getX(), location.getBlockY() - origin.getY(), location.getBlockZ() - origin.getZ());
                    drawManager.getDrawer(SelectionType.CUBOID).draw(player, shiftedRegion, true);
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
                    // Clear cache if present
                    if (settings.cacheLocations()) {
                        final SelectionCache cache = user.getSelectionCache();
                        if (cache != null) {
                            cache.getVectors().clear();
                            user.setSelectionCache(null);
                        }
                    }
                    continue;
                }

                final SelectionType selectionType = SelectionType.fromKey(selector.getTypeName());
                if (selectionType != null)
                    drawSelection(player, user, region, selectionType);
            }
        }
    }

    private void drawSelection(final Player player, final User user, final Region region, final SelectionType selectionType) {
        if (settings.cacheLocations()) {
            final SimpleVector minimumPoint = regionHelper.getMinimumPoint(region);
            final SimpleVector maximumPoint = regionHelper.getMaximumPoint(region);
            SelectionCache cache = user.getSelectionCache();
            if (cache != null) {
                if (selectionType == cache.getSelectionType()
                        && cache.getMinimum().equals(minimumPoint) && cache.getMaximum().equals(maximumPoint)) {
                    final Location location = new Location(player.getWorld(), 0, 0, 0);
                    for (final SimpleVector vector : cache.getVectors()) {
                        location.setX(vector.getX());
                        location.setY(vector.getY());
                        location.setZ(vector.getZ());
                        if (settings.sendParticlesToAll()) {
                            ParticleEffectUtil.playEffect(settings.getParticle(), location, 0, 1, settings.getParticleViewDistance());
                        } else {
                            ParticleEffectUtil.playEffect(settings.getParticle(), location, 0, 1, settings.getParticleViewDistance(), player);
                        }
                    }
                    return;
                }
            } else {
                cache = new SelectionCache();
                user.setSelectionCache(cache);
            }

            // If there's a new region, reset the cache and recalculate vectors
            cache.setMinimum(minimumPoint);
            cache.setMaximum(maximumPoint);
            cache.setSelectionType(selectionType);
            cache.getVectors().clear();
        }

        drawManager.getDrawer(selectionType).draw(player, region);
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

    public Version getVersion() {
        return version;
    }

    public Version getNewestVersion() {
        return newestVersion;
    }

    public IRegionHelper getRegionHelper() {
        return regionHelper;
    }
}
