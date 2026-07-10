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

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import eu.kennytv.worldeditsui.compat.Vector3D;
import eu.kennytv.worldeditsui.drawer.base.DrawContext;
import eu.kennytv.worldeditsui.drawer.base.DrawedType;
import eu.kennytv.worldeditsui.drawer.base.DrawerBase;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class PolyhedralDrawer extends DrawerBase {

    PolyhedralDrawer(final WorldEditSUIPlugin plugin) {
        super(plugin);
    }

    @Override
    public void draw(final Player player, final Region region, final DrawedType drawedType) {
        if (!hasValidSize(player, region)) return;

        final DrawContext context = createContext(player, region, drawedType);
        if (context == null) return;

        final Vector3D[][] triangles = plugin.getRegionHelper().getTriangles((ConvexPolyhedralRegion) region);
        // Neighboring triangles share their edges, so track which edges have already been drawn
        final Set<Edge> drawnEdges = new HashSet<>();
        final Location location = new Location(player.getWorld(), 0, 0, 0);
        for (final Vector3D[] triangle : triangles) {
            for (int i = 0; i < triangle.length; i++) {
                final Vector3D from = triangle[i];
                final Vector3D to = triangle[(i + 1) % triangle.length];
                if (drawnEdges.add(new Edge(from, to))) {
                    connect(context, location, from, to);
                }
            }
        }
    }

    private void connect(final DrawContext context, final Location location, final Vector3D from, final Vector3D to) {
        final double dx = to.getX() - from.getX();
        final double dy = to.getY() - from.getY();
        final double dz = to.getZ() - from.getZ();
        // Draw through the centers of the selected blocks
        double x = from.getX() + 0.5;
        double y = from.getY() + 0.5;
        double z = from.getZ() + 0.5;
        final int ticks = (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * settings.getParticlesPerBlock());
        if (ticks == 0) {
            location.setX(x);
            location.setY(y);
            location.setZ(z);
            context.playEffect(location);
            return;
        }

        final double stepX = dx / ticks;
        final double stepY = dy / ticks;
        final double stepZ = dz / ticks;
        for (int i = 0; i <= ticks; i++) {
            location.setX(x);
            location.setY(y);
            location.setZ(z);
            context.playEffect(location);
            x += stepX;
            y += stepY;
            z += stepZ;
        }
    }

    private static final class Edge {

        private final Vector3D a;
        private final Vector3D b;

        private Edge(final Vector3D from, final Vector3D to) {
            // Make sure the same edge listed in reverse by a neighboring triangle is caught
            if (compare(from, to) <= 0) {
                this.a = from;
                this.b = to;
            } else {
                this.a = to;
                this.b = from;
            }
        }

        private static int compare(final Vector3D first, final Vector3D second) {
            int compare = Double.compare(first.getX(), second.getX());
            if (compare != 0) return compare;
            compare = Double.compare(first.getY(), second.getY());
            if (compare != 0) return compare;
            return Double.compare(first.getZ(), second.getZ());
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Edge edge = (Edge) o;
            return a.equals(edge.a) && b.equals(edge.b);
        }

        @Override
        public int hashCode() {
            int result = a.hashCode();
            result = 31 * result + b.hashCode();
            return result;
        }
    }
}
