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

package eu.kennytv.worldeditsui.compat.we6;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.polyhedron.Triangle;
import com.sk89q.worldedit.session.ClipboardHolder;
import eu.kennytv.worldeditsui.compat.RegionHelper;
import eu.kennytv.worldeditsui.compat.Vector2D;
import eu.kennytv.worldeditsui.compat.Vector3D;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public final class RegionHelper6 implements RegionHelper {

    @Override
    public Vector3D getRadius(final EllipsoidRegion region) {
        final Vector radius = region.getRadius();
        return new Vector3D(radius.getX(), radius.getY(), radius.getZ());
    }

    @Override
    public Vector3D getRadius(final EllipsoidRegion region, final double offX, final double offY, final double offZ) {
        final Vector radius = region.getRadius();
        return new Vector3D(radius.getX() + offX, radius.getY() + offY, radius.getZ() + offZ);
    }

    @Override
    public Vector3D getRadius(final CylinderRegion region, final double offX, final double offZ) {
        final com.sk89q.worldedit.Vector2D radius = region.getRadius();
        return new Vector3D(radius.getX() + offX, region.getHeight(), radius.getZ() + offZ);
    }


    @Override
    public Vector3D getCenter(final Region region) {
        final Vector center = region.getCenter();
        return new Vector3D(center.getX(), center.getY(), center.getZ());
    }

    @Override
    public Vector3D getCenter(final Region region, final double offX, final double offY, final double offZ) {
        final Vector center = region.getCenter();
        return new Vector3D(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
    }

    @Override
    public Vector3D getMinimumPoint(final Region region) {
        final Vector minimum = region.getMinimumPoint();
        return new Vector3D(minimum.getX(), minimum.getY(), minimum.getZ());
    }

    @Override
    public Vector3D getMaximumPoint(final Region region) {
        final Vector maximum = region.getMaximumPoint();
        return new Vector3D(maximum.getX(), maximum.getY(), maximum.getZ());
    }

    @Override
    public Vector3D getOrigin(final Clipboard clipboard) {
        final Vector origin = clipboard.getOrigin();
        return new Vector3D(origin.getX(), origin.getY(), origin.getZ());
    }

    @Override
    public Region transformAndReShift(final ClipboardHolder holder, final Region region) {
        final Transform transform = holder.getTransform();
        final Vector origin = holder.getClipboard().getOrigin();
        final Vector oldMin = region.getMinimumPoint();
        final Vector oldMax = region.getMaximumPoint();
        final Vector offset = oldMin.subtract(origin);
        final Vector min = origin.add(transform.apply(offset));
        final Vector max = min.add(transform.apply(oldMax.subtract(oldMin)));
        return new CuboidRegion(region.getWorld(), min, max);
    }

    @Override
    public Region shift(final Region region, final double x, final double y, final double z) {
        try {
            region.shift(new Vector(x, y, z));
        } catch (final RegionOperationException ignored) {
        }
        return region;
    }

    @Override
    public Material getWand(final WorldEditPlugin plugin) {
        return new ItemStack(plugin.getLocalConfiguration().wandItem).getType();
    }

    @Override
    public Vector2D[] getPoints(final Polygonal2DRegion region) {
        final List<BlockVector2D> originalVectors = region.polygonize(-1);
        final Vector2D[] vectors = new Vector2D[originalVectors.size()];
        int i = 0;
        for (final BlockVector2D vector : originalVectors) {
            vectors[i++] = new Vector2D(vector.getX(), vector.getZ());
        }
        return vectors;
    }

    @Override
    public Vector3D[][] getTriangles(final ConvexPolyhedralRegion region) {
        final Collection<Triangle> originalTriangles = region.getTriangles();
        final Vector3D[][] triangles = new Vector3D[originalTriangles.size()][];
        int i = 0;
        for (final Triangle triangle : originalTriangles) {
            final Vector3D[] vertices = new Vector3D[3];
            for (int j = 0; j < 3; j++) {
                final Vector vertex = triangle.getVertex(j);
                vertices[j] = new Vector3D(vertex.getX(), vertex.getY(), vertex.getZ());
            }
            triangles[i++] = vertices;
        }
        return triangles;
    }

    @Override
    public int getTriangleCount(final ConvexPolyhedralRegion region) {
        return region.getTriangles().size();
    }
}
