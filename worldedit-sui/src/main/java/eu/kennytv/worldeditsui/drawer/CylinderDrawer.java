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

package eu.kennytv.worldeditsui.drawer;

import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.Region;
import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import eu.kennytv.worldeditsui.compat.SimpleVector;
import eu.kennytv.worldeditsui.drawer.base.DrawerBase;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class CylinderDrawer extends DrawerBase {

    CylinderDrawer(final WorldEditSUIPlugin plugin) {
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
        final Location location = new Location(player.getWorld(), center.getX(), bottom, center.getZ());
        final double wideGrid = Math.PI / (settings.getParticlesPerBlock() * max * 2);
        final int heightSpace = (int) (settings.getParticleSpace() * height);
        drawCurves(player, width, length, heightSpace == 0 ? height : height / heightSpace, location, wideGrid, heightSpace == 0 ? 1 : heightSpace);
        location.setY(top);
        drawCurves(player, width, length, 1, location, wideGrid, heightSpace == 0 ? 1 : heightSpace);

        if (settings.hasAdvancedGrid()) {
            location.setY(bottom);
            final double wideInterval = Math.PI / (settings.getParticlesPerGridBlock() * max / 10D);
            drawCurves(player, width, length, height * settings.getParticlesPerGridBlock(), location, wideInterval, settings.getParticleGridSpace());
            drawGrid(player, width, length, top, location, true);
            drawGrid(player, length, width, top, location, false);
        }
    }

    private void drawGrid(final Player player, final int width, final int length, final int top, final Location location, final boolean xAxis) {
        final double x = location.getX();
        final double z = location.getZ();
        final double bottom = location.getY();
        final int gap = settings.getParticlesPerBlock() * (((width * length) / AREA_FACTOR) + 1);
        final int ticks = 2 * (width - 1) / gap;
        if (xAxis) location.setX(location.getX() - width);
        else location.setZ(location.getZ() - width);

        for (int i = 0; i < ticks; i++) {
            if (xAxis) location.setX(location.getX() + gap);
            else location.setZ(location.getZ() + gap);
            final double delta = Math.abs(xAxis ? location.getX() - x : location.getZ() - z);
            // Thanks to a beautiful buddy of mine that took an our of his life to come up with this formula by himself, even though it's on Wikipedia 👀
            final double radius = ((double) length / width) * Math.sqrt((width * width) - (delta * delta));
            final int gridTicks = (int) (radius * settings.getParticlesPerGridBlock() * 2);
            if (xAxis) location.setZ(location.getZ() - radius);
            else location.setX(location.getX() - radius);
            for (int j = 0; j < gridTicks; j++) {
                if (xAxis) location.setZ(location.getZ() + settings.getParticleGridSpace());
                else location.setX(location.getX() + settings.getParticleGridSpace());
                playEffect(location, player);
                location.setY(top);
                playEffect(location, player);
                location.setY(bottom);
            }
            if (xAxis) location.setZ(z);
            else location.setX(x);
        }
        if (xAxis) location.setX(x);
        else location.setZ(z);
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
}
