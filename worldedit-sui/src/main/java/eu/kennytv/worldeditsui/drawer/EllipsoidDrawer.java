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
import eu.kennytv.worldeditsui.compat.Vector3D;
import eu.kennytv.worldeditsui.drawer.base.DrawContext;
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

        final DrawContext context = createContext(player, region, drawedType);
        if (context == null) return;

        final Vector3D radius = plugin.getRegionHelper().getRadius((EllipsoidRegion) region, 1.5, 1.4, 1.5);
        final int width = (int) radius.getX();
        final int length = (int) radius.getZ();
        final int height = (int) radius.getY();

        final Vector3D center = plugin.getRegionHelper().getCenter(region, 0.5, 0.5, 0.5);
        final Location location = new Location(player.getWorld(), center.getX(), center.getY(), center.getZ());
        // Three great circles are much cheaper than a grid over the full surface
        drawGreatCircles(context, location, width, height, length);

        if (settings.hasAdvancedGrid(drawedType)) {
            final int max = Math.max(length, width);
            final double heightGrid = Math.PI / (settings.getParticlesPerGridBlock(drawedType) * height);
            final double wideGrid = Math.PI / (settings.getParticlesPerGridBlock(drawedType) * max / 5D);
            showGrid(context, width, length, height, location, heightGrid, wideGrid);
        }
    }

    private void drawGreatCircles(final DrawContext context, final Location location, final double width, final double height, final double length) {
        final int particlesPerBlock = settings.getParticlesPerBlock();
        // Horizontal
        double interval = 1D / (Math.max(width, length) * particlesPerBlock);
        for (double i = 0; i < 2 * Math.PI; i += interval) {
            playEffect(context, location, width * Math.cos(i), 0, length * Math.sin(i));
        }

        // Vertical
        interval = 1D / (Math.max(width, height) * particlesPerBlock);
        for (double i = 0; i < 2 * Math.PI; i += interval) {
            playEffect(context, location, width * Math.cos(i), height * Math.sin(i), 0);
        }

        interval = 1D / (Math.max(length, height) * particlesPerBlock);
        for (double i = 0; i < 2 * Math.PI; i += interval) {
            playEffect(context, location, 0, height * Math.sin(i), length * Math.cos(i));
        }
    }

    private void playEffect(final DrawContext context, final Location location, final double x, final double y, final double z) {
        context.playEffect(location.add(x, y, z));
        location.subtract(x, y, z);
    }

    private void showGrid(final DrawContext context, final int width, final int length, final int height, final Location location, final double heightGrid, final double wideGrid) {
        for (double i = 0; i <= Math.PI; i += wideGrid) {
            for (double j = 0; j <= 2 * Math.PI; j += heightGrid) {
                final double x = width * Math.cos(j) * Math.sin(i);
                final double y = height * Math.cos(i);
                final double z = length * Math.sin(j) * Math.sin(i);

                context.playEffect(location.add(x, y, z));
                location.subtract(x, y, z);
            }
        }
    }
}
