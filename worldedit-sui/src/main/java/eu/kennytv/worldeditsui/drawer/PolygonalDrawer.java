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

import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import eu.kennytv.worldeditsui.compat.Vector2D;
import eu.kennytv.worldeditsui.drawer.base.DrawContext;
import eu.kennytv.worldeditsui.drawer.base.DrawedType;
import eu.kennytv.worldeditsui.drawer.base.DrawerBase;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class PolygonalDrawer extends DrawerBase {

    PolygonalDrawer(final WorldEditSUIPlugin plugin) {
        super(plugin);
    }

    @Override
    public void draw(final Player player, final Region region, final DrawedType drawedType) {
        if (!hasValidSize(player, region)) return;

        final DrawContext context = createContext(player, region, drawedType);
        if (context == null) return;

        final Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;
        final Vector2D[] points = plugin.getRegionHelper().getPoints(polyRegion);
        final int length = points.length;
        // The points are the north-west corners of the boundary columns. Shift each point
        // so that lines go around the blocks instead of into them
        final double[] xs = new double[length];
        final double[] zs = new double[length];
        computeOutlineCorners(points, xs, zs);

        final int bottom = ((FlatRegion) region).getMinimumY();
        final int height = region.getHeight();
        final int top = bottom + height;
        final int upwardsTicks = height * settings.getParticlesPerBlock();
        final Location location = new Location(player.getWorld(), xs[0], bottom, zs[0]);
        for (int i = 1; i <= length; i++) {
            final int index = i % length;
            connect(context, xs[index] - xs[i - 1], zs[index] - zs[i - 1], bottom, top, upwardsTicks, location);
            // Just to avoid minor inaccuracy because of the repeated additions
            location.setX(xs[index]);
            location.setZ(zs[index]);
        }
    }

    private void computeOutlineCorners(final Vector2D[] points, final double[] xs, final double[] zs) {
        final int length = points.length;
        // Shoelace formula to determine the polygon's winding order
        double area = 0;
        for (int i = 0; i < length; i++) {
            final Vector2D current = points[i];
            final Vector2D next = points[(i + 1) % length];
            area += current.getX() * next.getZ() - next.getX() * current.getZ();
        }

        final int sign = area > 0 ? 1 : -1;
        for (int i = 0; i < length; i++) {
            final Vector2D previous = points[(i + length - 1) % length];
            final Vector2D next = points[(i + 1) % length];
            // The sum of the two adjacent edge vectors is next-previous; its outward
            // perpendicular decides per axis whether the outer block corner is at +1
            final double outX = sign * (next.getZ() - previous.getZ());
            final double outZ = sign * (previous.getX() - next.getX());
            xs[i] = points[i].getX() + (outX > 0 ? 1 : 0);
            zs[i] = points[i].getZ() + (outZ > 0 ? 1 : 0);
        }
    }

    private void connect(final DrawContext context, final double deltaX, final double deltaZ, final int bottom, final int top, final int upwardsTicks, final Location location) {
        final double length = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        final double factor = length * settings.getParticlesPerBlock();
        final double x = deltaX / factor;
        final double z = deltaZ / factor;
        final int ticks = (int) factor;

        for (int i = 0; i < ticks; i++) {
            context.playEffect(location.add(x, 0, z));
            location.setY(top);
            context.playEffect(location);
            location.setY(bottom);
        }
        for (int j = 1; j < upwardsTicks; j++) {
            location.setY(location.getY() + settings.getParticleSpace());
            context.playEffect(location);
        }
        location.setY(bottom);
    }
}
