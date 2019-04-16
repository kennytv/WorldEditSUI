package eu.kennytv.worldeditcui.drawer;

import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.Region;
import eu.kennytv.worldeditcui.WorldEditCUIPlugin;
import eu.kennytv.worldeditcui.compat.SimpleVector;
import eu.kennytv.worldeditcui.drawer.base.DrawerBase;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class CylinderDrawer extends DrawerBase {

    CylinderDrawer(final WorldEditCUIPlugin plugin) {
        super(plugin);
    }

    @Override
    public void draw(final Player player, final Region region) {
        final SimpleVector radius = plugin.getRegionHelper().getRadius((CylinderRegion) region, 1.3, 1.3);
        final int width = (int) radius.getX();
        final int length = (int) radius.getZ();
        final int height = (int) radius.getY();
        final int max = Math.max(length, width);
        final int bottom = ((FlatRegion) region).getMinimumY();
        final int top = ((FlatRegion) region).getMaximumY() + 1;

        final SimpleVector center = plugin.getRegionHelper().getCenter(region, 0.5, 0, 0.5);
        final Location location = new Location(plugin.getServer().getWorld(region.getWorld().getName()), center.getX(), bottom, center.getZ());
        final double wideGrid = Math.PI / (settings.getParticlesPerBlock() * max * 2);
        final int heightSpace = (int) (settings.getParticleSpace() * height);
        drawCurves(player, width, length, heightSpace == 0 ? height : height / heightSpace, location, wideGrid, heightSpace == 0 ? 1 : heightSpace);
        location.setY(top);
        drawCurves(player, width, length, 1, location, wideGrid, heightSpace == 0 ? 1 : heightSpace);
        location.setY(bottom);

        if (settings.hasAdvancedGrid()) {
            final double wideInterval = Math.PI / (settings.getParticlesPerGridBlock() * max / 10D);
            drawCurves(player, width, length, (int) (height / settings.getParticleGridSpace()), location, wideInterval, settings.getParticleGridSpace());
            drawGrid(player, width, length, location, true);
            drawGrid(player, length, width, location, false);
            location.setY(top);
            drawGrid(player, width, length, location, true);
            drawGrid(player, length, width, location, false);
        }
    }

    private void drawCurves(final Player player, final int width, final int length, final int ticks, final Location location, final double wideGrid, final double heightSpace) {
        final double y = location.getY();
        for (double i = 0; i <= 2 * Math.PI; i += wideGrid) {
            final double x = width * Math.cos(i);
            final double z = length * Math.sin(i);
            location.add(x, -heightSpace, z);
            for (int j = 0; j < ticks; j++) {
                playEffect(location.add(0, heightSpace, 0), player);
            }
            location.subtract(x, 0, z).setY(y);
        }
    }

    private void drawGrid(final Player player, final int width, final int length, final Location location, final boolean xAxis) {
        final double x = location.getX();
        final double z = location.getZ();
        final int gap = settings.getParticlesPerBlock() * (((width * length) / AREA_FACTOR) + 1);
        if (xAxis) location.setX(location.getX() - width);
        else location.setZ(location.getZ() - width);

        final int ticks = 2 * (width - 1) / gap;
        final double halfTicks = ticks / 2D;
        for (int i = 0; i < ticks; i++) {
            final double midRadius = length * (i <= halfTicks ? Math.min(0.7 + (i * 0.1), 1) : 1 - ((i - halfTicks) * 0.1));
            if (xAxis) location.add(gap, 0, -midRadius);
            else location.add(-midRadius, 0, gap);

            final int gridTicks = (int) (settings.getParticlesPerGridBlock() * midRadius * 2);
            for (int j = 0; j < gridTicks; j++) {
                if (xAxis) location.setZ(location.getZ() + settings.getParticleGridSpace());
                else location.setX(location.getX() + settings.getParticleGridSpace());
                playEffect(location, player);
            }
            if (xAxis) location.setZ(z);
            else location.setX(x);
        }
        if (xAxis) location.setX(x);
        else location.setZ(z);
    }
}
