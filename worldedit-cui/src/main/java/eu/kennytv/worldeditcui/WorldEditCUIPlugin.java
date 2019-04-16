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

public final class WorldEditCUIPlugin extends JavaPlugin {
    private static final String PREFIX = "§8[§eWorldEditCUI§8] ";
    private IRegionHelper regionHelper;
    private UserManager userManager;
    private Settings settings;
    private DrawManager drawManager;
    private WorldEditPlugin worldEditPlugin;
    private String version;

    @Override
    public void onEnable() {
        version = getDescription().getVersion();
        getLogger().info("Plugin by KennyTV");

        userManager = new UserManager();
        settings = new Settings(this);
        drawManager = new DrawManager(this);

        final PluginManager pm = getServer().getPluginManager();
        worldEditPlugin = ((WorldEditPlugin) pm.getPlugin("WorldEdit"));

        final String majorVersion = worldEditPlugin.getDescription().getVersion().split("\\.", 2)[0];
        if (majorVersion.matches("[0-9]+")) {
            regionHelper = Integer.parseInt(majorVersion) >= 7 ? new eu.kennytv.worldeditcui.compat.we7.RegionHelper() : new eu.kennytv.worldeditcui.compat.we6.RegionHelper();
        } else {
            // WE version undefined with FAWE/AWE on 1.13+
            regionHelper = new eu.kennytv.worldeditcui.compat.we7.RegionHelper();
        }

        getServer().getOnlinePlayers().forEach(p -> userManager.createUser(p));

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(userManager), this);
        pm.registerEvents(new WESelectionListener(this), this);
        getCommand("worldeditcui").setExecutor(new WECUICommand(this));
        getServer().getScheduler().runTaskTimerAsynchronously(this, this::updateSelections, settings.getParticleSendIntervall(), settings.getParticleSendIntervall());
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (!settings.isExpiryEnabled()) return;
            userManager.getExpireTimestamps().entrySet().removeIf(entry -> {
                final boolean remove = entry.getValue() < System.currentTimeMillis();
                if (remove && settings.hasExpireMessage())
                    getServer().getPlayer(entry.getKey()).sendMessage(PREFIX + "§7The selection particles are no longer displayed, because you didn't change it for a while.");
                return remove;
            });
        }, 20, 20);
        new MetricsLite(this);
    }

    private void updateSelections() {
        getServer().getOnlinePlayers().forEach(player -> {
            if (!player.isOnline()) return;

            final User user = userManager.getUser(player);
            if (user == null) return;
            if (!user.isSelectionShown() && !user.isClipboardShown()) return;
            if (settings.isExpiryEnabled() && !userManager.getExpireTimestamps().containsKey(player.getUniqueId()))
                return;
            if (!settings.getPermission().isEmpty() && !player.hasPermission(settings.getPermission())) return;

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
