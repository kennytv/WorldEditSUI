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

package eu.kennytv.worldeditsui.drawer;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import eu.kennytv.worldeditsui.compat.SimpleVector;
import eu.kennytv.worldeditsui.drawer.base.DrawedType;
import eu.kennytv.worldeditsui.drawer.base.DrawerBase;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class EllipsoidDrawer extends DrawerBase {

    EllipsoidDrawer(final WorldEditSUIPlugin plugin) {
        super(plugin);
    }

    @Override
    public void draw(final Player player, final Region region, final DrawedType drawedType) {
        if (!hasValidSize(player, region)) return;

        final SimpleVector radius = plugin.getRegionHelper().getRadius((EllipsoidRegion) region, 1.5, 1.4, 1.5);
        final int width = (int) radius.getX();
        final int length = (int) radius.getZ();
        final int height = (int) radius.getY();
        final int max = Math.max(length, width);

        final SimpleVector center = plugin.getRegionHelper().getCenter(region, 0.5, 0.5, 0.5);
        final Location location = new Location(player.getWorld(), center.getX(), center.getY(), center.getZ());
        final double heightInterval = Math.PI / (settings.getParticlesPerBlock() * height);
        final double wideInterval = Math.PI / (settings.getParticlesPerBlock() * max / 10D);
        showGrid(player, width, length, height, location, wideInterval, heightInterval);

        if (settings.hasAdvancedGrid(drawedType)) {
            final double heightGrid = Math.PI / (settings.getParticlesPerGridBlock(drawedType) * height);
            final double wideGrid = Math.PI / (settings.getParticlesPerGridBlock(drawedType) * max / 5D);
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
