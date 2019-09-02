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

package eu.kennytv.worldeditsui.util;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class RegionHelper {

    private static final BlockVector2D[] A = new BlockVector2D[0];

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


    public Material getWand(final WorldEditPlugin plugin) {
        return new ItemStack(plugin.getLocalConfiguration().wandItem).getType();
    }

    public BlockVector2D[] getPoints(final Polygonal2DRegion region) {
        final List<BlockVector2D> originalVectors = region.polygonize(-1);
        return originalVectors.toArray(A);
    }
}
