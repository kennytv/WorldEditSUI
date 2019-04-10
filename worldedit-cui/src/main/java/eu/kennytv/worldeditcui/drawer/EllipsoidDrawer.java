package eu.kennytv.worldeditcui.drawer;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import eu.kennytv.worldeditcui.WorldEditCUIPlugin;
import eu.kennytv.worldeditcui.drawer.base.DrawerBase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class EllipsoidDrawer extends DrawerBase {

    EllipsoidDrawer(final WorldEditCUIPlugin plugin) {
        super(plugin);
    }

    @Override
    public void draw(final Player player, final Region region) {
        final Vector radius = plugin.getRegionHelper().getRadius((EllipsoidRegion) region, 1.5, 1.4, 1.5);
        final int width = (int) radius.getX();
        final int length = (int) radius.getZ();
        final int height = (int) radius.getY();
        final int max = Math.max(length, width);

        final Vector center = plugin.getRegionHelper().getCenter(region, 0.5, 0.5, 0.5);
        final Location location = new Location(plugin.getServer().getWorld(region.getWorld().getName()), center.getX(), center.getY(), center.getZ());
        final double heightInterval = Math.PI / (settings.getParticlesPerBlock() * height);
        final double wideInterval = Math.PI / (settings.getParticlesPerBlock() * max / 10D);
        showGrid(player, width, length, height, location, wideInterval, heightInterval);

        if (settings.hasAdvancedGrid()) {
            final double heightGrid = Math.PI / (settings.getParticlesPerGridBlock() * height);
            final double wideGrid = Math.PI / (settings.getParticlesPerGridBlock() * max / 5D);
            showGrid(player, width, length, height, location, heightGrid, wideGrid);
        }
    }

    private void showGrid(final Player player, final int width, final int length, final int height, final Location location, final double heightGrid, final double wideGrid) {
        for (double i = 0; i <= Math.PI; i += wideGrid) {
            for (double j = 0; j <= 2 * Math.PI; j += heightGrid) {
                final double x = width * Math.cos(j) * Math.sin(i);
                final double y = height * Math.cos(i);
                final double z = length * Math.sin(j) * Math.sin(i);

                playEffect(location.add(x, y, z), player);
                location.subtract(x, y, z);
            }
        }
    }
}
