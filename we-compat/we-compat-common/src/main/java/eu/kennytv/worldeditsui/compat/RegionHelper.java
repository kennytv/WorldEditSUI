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

package eu.kennytv.worldeditsui.compat;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Material;

public interface RegionHelper {

    SimpleVector getRadius(EllipsoidRegion region);

    SimpleVector getRadius(EllipsoidRegion region, double offX, double offY, double offZ);

    SimpleVector getRadius(CylinderRegion region, double offX, double offZ);

    SimpleVector getCenter(Region region);

    SimpleVector getCenter(Region region, double offX, double offY, double offZ);

    SimpleVector getMinimumPoint(Region region);

    SimpleVector getMaximumPoint(Region region);

    SimpleVector getOrigin(Clipboard clipboard);

    Region transformAndReShift(ClipboardHolder holder, Region region);

    Region shift(Region region, double x, double y, double z);

    Material getWand(WorldEditPlugin plugin);

    Simple2DVector[] getPoints(Polygonal2DRegion region);
}
