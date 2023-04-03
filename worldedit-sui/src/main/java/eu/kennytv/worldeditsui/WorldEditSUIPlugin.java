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

package eu.kennytv.worldeditsui;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import eu.kennytv.worldeditsui.command.WESUICommand;
import eu.kennytv.worldeditsui.compat.ProtectedRegionHelper;
import eu.kennytv.worldeditsui.compat.ProtectedRegionWrapper;
import eu.kennytv.worldeditsui.compat.RegionHelper;
import eu.kennytv.worldeditsui.compat.SelectionType;
import eu.kennytv.worldeditsui.compat.SimpleVector;
import eu.kennytv.worldeditsui.compat.we6.ProtectedRegionHelper6;
import eu.kennytv.worldeditsui.compat.we6.RegionHelper6;
import eu.kennytv.worldeditsui.compat.we7.ProtectedRegionHelper7;
import eu.kennytv.worldeditsui.compat.we7.RegionHelper7;
import eu.kennytv.worldeditsui.drawer.DrawManager;
import eu.kennytv.worldeditsui.drawer.base.DrawedType;
import eu.kennytv.worldeditsui.listener.PlayerJoinListener;
import eu.kennytv.worldeditsui.listener.PlayerQuitListener;
import eu.kennytv.worldeditsui.listener.WESelectionListener;
import eu.kennytv.worldeditsui.user.SelectionCache;
import eu.kennytv.worldeditsui.user.User;
import eu.kennytv.worldeditsui.user.UserManager;
import eu.kennytv.worldeditsui.util.Cancellable;
import eu.kennytv.worldeditsui.util.CancellableBukkitTask;
import eu.kennytv.worldeditsui.util.CancellableScheduledTask;
import eu.kennytv.worldeditsui.util.ParticleHelper;
import eu.kennytv.worldeditsui.util.Version;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public final class WorldEditSUIPlugin extends JavaPlugin {

    private static final boolean FOLIA = hasClass("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
    private static final String PREFIX = "§8[§eWorldEditSUI§8] ";
    private RegionHelper regionHelper;
    private ProtectedRegionHelper protectedRegionHelper;
    private ParticleHelper particleHelper;
    private UserManager userManager;
    private Settings settings;
    private DrawManager drawManager;
    private WorldEditPlugin worldEditPlugin;
    private Version version;
    private Version newestVersion;
    private Cancellable expiryTask;
    private Cancellable persistentTogglesTask;
    private boolean worldGuardEnabled;

    @Override
    public void onEnable() {
        version = new Version(getDescription().getVersion());
        printEnableMessage();

        try {
            Class.forName("org.bukkit.Particle");
        } catch (final ClassNotFoundException e) {
            getLogger().severe("This plugin only supports Minecraft versions from 1.9 upwards.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        boolean isWorldEdit7 = true;
        try {
            Class.forName("com.sk89q.worldedit.math.Vector2");
        } catch (final ClassNotFoundException e) {
            isWorldEdit7 = false;
        }

        regionHelper = isWorldEdit7 ? new RegionHelper7() : new RegionHelper6();

        settings = new Settings(this);

        particleHelper = new ParticleHelper(settings);
        userManager = new UserManager(settings);
        drawManager = new DrawManager(this);

        final PluginManager pm = getServer().getPluginManager();
        worldEditPlugin = ((WorldEditPlugin) pm.getPlugin("WorldEdit"));
        worldGuardEnabled = pm.isPluginEnabled("WorldGuard");
        if (worldGuardEnabled) {
            protectedRegionHelper = isWorldEdit7 ? new ProtectedRegionHelper7() : new ProtectedRegionHelper6();
        }

        // Imagine someone using the server reload command 👀
        getServer().getOnlinePlayers().forEach(p -> userManager.createUser(p));

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(userManager), this);
        pm.registerEvents(new WESelectionListener(this), this);

        final PluginCommand command = getCommand("worldeditsui");
        command.setExecutor(new WESUICommand(this));
        command.setPermissionMessage(settings.getMessage("noPermission"));

        // Start tasks
        runRepeatedAsync(this::updateSelections, settings.getParticleSendInterval(), settings.getParticleSendInterval());
        reloadTasks();

        new Metrics(this, 5444);
    }

    @Override
    public void onDisable() {
        if (settings == null) return; // if instantly disabled

        settings.saveData();
        getServer().getOnlinePlayers().forEach(player -> userManager.deleteUser(player));
    }

    public Cancellable runRepeatedAsync(final Runnable runnable, final long delay, final long period) {
        if (FOLIA) {
            return new CancellableScheduledTask(getServer().getAsyncScheduler().runAtFixedRate(this, scheduledTask -> runnable.run(), delay * 50, period * 50, TimeUnit.MILLISECONDS));
        } else {
            return new CancellableBukkitTask(getServer().getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period));
        }
    }

    public Cancellable runRepeated(final Runnable runnable, final long delay, final long period) {
        if (FOLIA) {
            return new CancellableScheduledTask(getServer().getGlobalRegionScheduler().runAtFixedRate(this, task -> runnable.run(), delay, period));
        } else {
            return new CancellableBukkitTask(getServer().getScheduler().runTaskTimer(this, runnable, delay, period));
        }
    }

    public void runAsync(final Runnable runnable) {
        if (FOLIA) {
            getServer().getAsyncScheduler().runNow(this, task -> runnable.run());
        } else {
            getServer().getScheduler().runTaskAsynchronously(this, runnable);
        }
    }

    private void printEnableMessage() {
        getLogger().info("Plugin by kennytv");
        runAsync(() -> {
            updateAvailable();
            final int compare = version.compareTo(newestVersion);
            if (compare < 0) {
                getLogger().info("§cNewest version available: §aVersion " + newestVersion + "§c, you're on §a" + version);
            } else if (compare > 0) {
                if (version.getTag().equalsIgnoreCase("snapshot")) {
                    getLogger().info("§cYou're running a development version, please report bugs on the Discord server (https://discord.gg/vGCUzHq).");
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
            return version.compareTo(newVersion) < 0;
        } catch (final Exception ignored) {
            return false;
        }
    }

    public void reloadTasks() {
        // Start tasks if not already running, or cancel them if one is running but now disabled in the config
        if (expiryTask == null && settings.isExpiryEnabled()) {
            expiryTask = runRepeated(() -> {
                for (final User user : userManager.getUsers().values()) {
                    if (user.getExpireTimestamp() == 0) continue;
                    if (user.getExpireTimestamp() > System.currentTimeMillis()) continue;

                    user.setExpireTimestamp(0);
                    if (settings.hasExpireMessage()) {
                        getServer().getPlayer(user.getUuid()).sendMessage(settings.getMessage("idled"));
                    }
                }
            }, 20, 20);
        } else if (expiryTask != null && !settings.isExpiryEnabled()) {
            expiryTask.cancel();
            expiryTask = null;

            for (final User user : userManager.getUsers().values()) {
                user.setExpireTimestamp(-1);
            }
        }

        if (persistentTogglesTask == null && settings.hasPersistentToggles()) {
            // Check every 15 minutes
            persistentTogglesTask = runRepeatedAsync(() -> settings.saveData(), 18000, 18000);
        } else if (persistentTogglesTask != null && !settings.hasPersistentToggles()) {
            persistentTogglesTask.cancel();
            persistentTogglesTask = null;
        }
    }

    private void updateSelections() {
        for (final Player player : getServer().getOnlinePlayers()) {
            if (!player.isOnline()) continue;

            final User user = userManager.getUser(player);
            if (user == null) continue;
            // Check for sending restrictions
            if (!user.isSelectionShown() && !user.isClipboardShown()) continue;
            if (settings.isExpiryEnabled() && user.getExpireTimestamp() == 0)
                continue;
            if (settings.getPermission() != null && !player.hasPermission(settings.getPermission())) continue;
            if (settings.getMaxPing() != 0 && player.spigot().getPing() > settings.getMaxPing()) continue;

            final LocalSession session = worldEditPlugin.getSession(player);
            final RegionSelector selector = session.getRegionSelector(new BukkitWorld(player.getWorld()));

            // Selected WorldGuard region
            final ProtectedRegionWrapper selectedWGRegion = user.getSelectedWGRegion();
            if (selectedWGRegion != null) {
                drawSelection(player, user, selectedWGRegion.getRegion(), selectedWGRegion.getSelectionType(), DrawedType.WG_REGION);
            }

            // Clipboard
            if (user.isClipboardShown()) {
                try {
                    final ClipboardHolder holder = session.getClipboard();
                    final Clipboard clipboard = holder.getClipboard();
                    final Location location = player.getLocation();
                    final SimpleVector origin = regionHelper.getOrigin(clipboard);

                    // Transform the clipboard if necessary
                    final Transform transform = holder.getTransform();
                    final Region region = transform.isIdentity() ? clipboard.getRegion() : regionHelper.transformAndReShift(holder, clipboard.getRegion());

                    // Shift the transformed region relative to the player
                    final Region shiftedRegion = regionHelper.shift(region,
                            location.getBlockX() - origin.getX(), location.getBlockY() - origin.getY(), location.getBlockZ() - origin.getZ());
                    drawManager.getDrawer(SelectionType.CUBOID).draw(player, shiftedRegion, DrawedType.CLIPBOARD);
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
                        user.clearCache(DrawedType.SELECTED);
                    }
                    continue;
                }

                final SelectionType selectionType = SelectionType.fromKey(selector.getTypeName());
                if (selectionType != null) {
                    drawSelection(player, user, region, selectionType, DrawedType.SELECTED);
                }
            }
        }
    }

    private void drawSelection(final Player player, final User user, final Region region, final SelectionType selectionType, final DrawedType drawedType) {
        if (settings.cacheLocations()) {
            final SimpleVector minimumPoint = regionHelper.getMinimumPoint(region);
            final SimpleVector maximumPoint = regionHelper.getMaximumPoint(region);
            SelectionCache cache = user.getSelectionCache(drawedType);
            if (cache != null) {
                if (selectionType == cache.getSelectionType()
                        && cache.getMinimum().equals(minimumPoint) && cache.getMaximum().equals(maximumPoint)) {
                    final Location location = new Location(player.getWorld(), 0, 0, 0);
                    for (final SimpleVector vector : cache.getVectors()) {
                        location.setX(vector.getX());
                        location.setY(vector.getY());
                        location.setZ(vector.getZ());
                        if (settings.sendParticlesToAll(drawedType)) {
                            particleHelper.playEffectToAll(settings.getParticle(drawedType), settings.getOthersParticle(drawedType), location, player);
                        } else {
                            particleHelper.playEffect(settings.getParticle(drawedType), location, player);
                        }
                    }
                    return;
                }
            } else {
                cache = new SelectionCache();
                user.setSelectionCache(drawedType, cache);
            }

            // If there's a new region, reset the cache and recalculate vectors
            cache.setMinimum(minimumPoint);
            cache.setMaximum(maximumPoint);
            cache.setSelectionType(selectionType);
            cache.getVectors().clear();
        }

        // Current selection
        drawManager.getDrawer(selectionType).draw(player, region, drawedType);
    }

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    public DrawManager getDrawManager() {
        return drawManager;
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

    public RegionHelper getRegionHelper() {
        return regionHelper;
    }

    @MonotonicNonNull
    public ProtectedRegionHelper getProtectedRegionHelper() {
        return protectedRegionHelper;
    }

    public ParticleHelper getParticleHelper() {
        return particleHelper;
    }

    public boolean isWorldGuardEnabled() {
        return worldGuardEnabled;
    }

    private static boolean hasClass(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }
}
