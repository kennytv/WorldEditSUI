/*
 * WorldEditSUI - https://git.io/wesui
 * Copyright (C) 2018-2020 KennyTV (https://github.com/KennyTV)
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
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class RegionHelper6 implements RegionHelper {

    @Override
    public SimpleVector getRadius(final EllipsoidRegion region) {
        final Vector radius = region.getRadius();
        return new SimpleVector(radius.getX(), radius.getY(), radius.getZ());
    }

    @Override
    public SimpleVector getRadius(final EllipsoidRegion region, final double offX, final double offY, final double offZ) {
        final Vector radius = region.getRadius();
        return new SimpleVector(radius.getX() + offX, radius.getY() + offY, radius.getZ() + offZ);
    }

    @Override
    public SimpleVector getRadius(final CylinderRegion region, final double offX, final double offZ) {
        final Vector2D radius = region.getRadius();
        return new SimpleVector(radius.getX() + offX, region.getHeight(), radius.getZ() + offZ);
    }


    @Override
    public SimpleVector getCenter(final Region region) {
        final Vector center = region.getCenter();
        return new SimpleVector(center.getX(), center.getY(), center.getZ());
    }

    @Override
    public SimpleVector getCenter(final Region region, final double offX, final double offY, final double offZ) {
        final Vector center = region.getCenter();
        return new SimpleVector(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
    }

    @Override
    public SimpleVector getMinimumPoint(final Region region) {
        final Vector minimum = region.getMinimumPoint();
        return new SimpleVector(minimum.getX(), minimum.getY(), minimum.getZ());
    }

    @Override
    public SimpleVector getMaximumPoint(final Region region) {
        final Vector maximum = region.getMaximumPoint();
        return new SimpleVector(maximum.getX(), maximum.getY(), maximum.getZ());
    }

    @Override
    public SimpleVector getOrigin(final Clipboard clipboard) {
        final Vector origin = clipboard.getOrigin();
        return new SimpleVector(origin.getX(), origin.getY(), origin.getZ());
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
    public Simple2DVector[] getPoints(final Polygonal2DRegion region) {
        final List<BlockVector2D> originalVectors = region.polygonize(-1);
        final Simple2DVector[] vectors = new Simple2DVector[originalVectors.size()];
        int i = 0;
        for (final BlockVector2D vector : originalVectors) {
            vectors[i++] = new Simple2DVector(vector.getX(), vector.getZ());
        }
        return vectors;
    }
}
