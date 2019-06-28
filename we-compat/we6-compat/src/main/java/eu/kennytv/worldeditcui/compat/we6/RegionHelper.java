/*
 * WorldEditCUI - https://git.io/wecui
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

package eu.kennytv.worldeditcui.compat.we6;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.*;
import eu.kennytv.worldeditcui.compat.IRegionHelper;
import eu.kennytv.worldeditcui.compat.Simple2DVector;
import eu.kennytv.worldeditcui.compat.SimpleVector;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class RegionHelper implements IRegionHelper {

    @Override
    public SimpleVector getRadius(final EllipsoidRegion region) {
        final com.sk89q.worldedit.Vector radius = region.getRadius();
        return new SimpleVector(radius.getX(), radius.getY(), radius.getZ());
    }

    @Override
    public SimpleVector getRadius(final EllipsoidRegion region, final double offX, final double offY, final double offZ) {
        final com.sk89q.worldedit.Vector radius = region.getRadius();
        return new SimpleVector(radius.getX() + offX, radius.getY() + offY, radius.getZ() + offZ);
    }

    @Override
    public SimpleVector getRadius(final CylinderRegion region, final double offX, final double offZ) {
        final com.sk89q.worldedit.Vector2D radius = region.getRadius();
        return new SimpleVector(radius.getX() + offX, region.getHeight(), radius.getZ() + offZ);
    }


    @Override
    public SimpleVector getCenter(final Region region) {
        final com.sk89q.worldedit.Vector center = region.getCenter();
        return new SimpleVector(center.getX(), center.getY(), center.getZ());
    }

    @Override
    public SimpleVector getCenter(final Region region, final double offX, final double offY, final double offZ) {
        final com.sk89q.worldedit.Vector center = region.getCenter();
        return new SimpleVector(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
    }

    @Override
    public SimpleVector getMinimumPoint(final Region region) {
        final com.sk89q.worldedit.Vector minimum = region.getMinimumPoint();
        return new SimpleVector(minimum.getX(), minimum.getY(), minimum.getZ());
    }

    @Override
    public SimpleVector getMaximumPoint(final Region region) {
        final com.sk89q.worldedit.Vector maximum = region.getMaximumPoint();
        return new SimpleVector(maximum.getX(), maximum.getY(), maximum.getZ());
    }

    @Override
    public SimpleVector getOrigin(final Clipboard clipboard) {
        final com.sk89q.worldedit.Vector origin = clipboard.getOrigin();
        return new SimpleVector(origin.getX(), origin.getY(), origin.getZ());
    }

    @Override
    public Region shift(final Region region, final double x, final double y, final double z) {
        try {
            region.shift(new com.sk89q.worldedit.Vector(x, y, z));
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
