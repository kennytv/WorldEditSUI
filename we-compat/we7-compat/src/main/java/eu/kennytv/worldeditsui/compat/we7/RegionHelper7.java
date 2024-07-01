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

package eu.kennytv.worldeditsui.compat.we7;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.session.ClipboardHolder;
import eu.kennytv.worldeditsui.compat.RegionHelper;
import eu.kennytv.worldeditsui.compat.Simple2DVector;
import eu.kennytv.worldeditsui.compat.SimpleVector;
import java.util.Locale;
import org.bukkit.Material;

import java.util.List;

public final class RegionHelper7 implements RegionHelper {

    @Override
    public SimpleVector getRadius(final EllipsoidRegion region) {
        final Vector3 radius = region.getRadius();
        return new SimpleVector(radius.getX(), radius.getY(), radius.getZ());
    }

    @Override
    public SimpleVector getRadius(final EllipsoidRegion region, final double offX, final double offY, final double offZ) {
        final Vector3 radius = region.getRadius();
        return new SimpleVector(radius.getX() + offX, radius.getY() + offY, radius.getZ() + offZ);
    }

    @Override
    public SimpleVector getRadius(final CylinderRegion region, final double offX, final double offZ) {
        final Vector2 radius = region.getRadius();
        return new SimpleVector(radius.getX() + offX, region.getHeight(), radius.getZ() + offZ);
    }

    @Override
    public SimpleVector getCenter(final Region region) {
        final Vector3 center = region.getCenter();
        return new SimpleVector(center.getX(), center.getY(), center.getZ());
    }

    @Override
    public SimpleVector getCenter(final Region region, final double offX, final double offY, final double offZ) {
        final Vector3 center = region.getCenter();
        return new SimpleVector(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
    }

    @Override
    public SimpleVector getMinimumPoint(final Region region) {
        final BlockVector3 minimum = region.getMinimumPoint();
        return new SimpleVector(minimum.getX(), minimum.getY(), minimum.getZ());
    }

    @Override
    public SimpleVector getMaximumPoint(final Region region) {
        final BlockVector3 maximum = region.getMaximumPoint();
        return new SimpleVector(maximum.getX(), maximum.getY(), maximum.getZ());
    }

    @Override
    public SimpleVector getOrigin(final Clipboard clipboard) {
        final BlockVector3 origin = clipboard.getOrigin();
        return new SimpleVector(origin.getX(), origin.getY(), origin.getZ());
    }

    @Override
    public Region transformAndReShift(final ClipboardHolder holder, final Region region) {
        final Transform transform = holder.getTransform();
        final BlockVector3 origin = holder.getClipboard().getOrigin();
        final BlockVector3 oldMin = region.getMinimumPoint();
        final BlockVector3 oldMax = region.getMaximumPoint();
        final BlockVector3 offset = oldMin.subtract(origin);
        final BlockVector3 min = origin.add(transform.apply(offset.toVector3()).toBlockPoint());
        final BlockVector3 max = min.add(transform.apply(oldMax.subtract(oldMin).toVector3()).toBlockPoint());
        return new CuboidRegion(region.getWorld(), min, max);
    }

    @Override
    public Region shift(final Region region, final double x, final double y, final double z) {
        final Region clone = region.clone();
        try {
            clone.shift(BlockVector3.at(x, y, z));
        } catch (final RegionOperationException ignored) {
        }
        return clone;
    }

    @Override
    public Material getWand(final WorldEditPlugin plugin) {
        return Material.getMaterial(plugin.getLocalConfiguration().wandItem.toUpperCase(Locale.ROOT).replace("MINECRAFT:", ""));
    }

    @Override
    public Simple2DVector[] getPoints(final Polygonal2DRegion region) {
        final List<BlockVector2> originalVectors = region.polygonize(-1);
        final Simple2DVector[] vectors = new Simple2DVector[originalVectors.size()];
        int i = 0;
        for (final BlockVector2 vector : originalVectors) {
            vectors[i++] = new Simple2DVector(vector.getX(), vector.getZ());
        }
        return vectors;
    }
}
