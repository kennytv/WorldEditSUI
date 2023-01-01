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

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionType;
import eu.kennytv.worldeditsui.compat.ProtectedRegionHelper;
import eu.kennytv.worldeditsui.compat.ProtectedRegionWrapper;
import eu.kennytv.worldeditsui.compat.SelectionType;
import org.bukkit.World;

import java.util.Collections;
import java.util.Set;

public final class ProtectedRegionHelper7 implements ProtectedRegionHelper {

    private final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();

    @Override
    public ProtectedRegionWrapper getRegion(final World world, final String regionName) {
        final BukkitWorld worldWrapper = new BukkitWorld(world);
        final RegionManager regionManager = regionContainer.get(worldWrapper);
        if (regionManager == null) {
            return null;
        }

        final ProtectedRegion region = regionManager.getRegion(regionName);
        if (region == null) {
            return null;
        }

        final RegionType type = region.getType();
        switch (type) {
            case CUBOID:
                return new ProtectedRegionWrapper(new CuboidRegion(worldWrapper, region.getMinimumPoint(), region.getMaximumPoint()), SelectionType.CUBOID);
            case POLYGON:
                return new ProtectedRegionWrapper(new Polygonal2DRegion(worldWrapper, region.getPoints(),
                        region.getMinimumPoint().getY(), region.getMaximumPoint().getY()), SelectionType.POLYGON);
            case GLOBAL:
            default:
                return null;
        }
    }

    @Override
    public Set<String> getRegionNames(final World world) {
        final RegionManager regionManager = regionContainer.get(new BukkitWorld(world));
        if (regionManager == null) return Collections.emptySet();

        return regionManager.getRegions().keySet();
    }
}
