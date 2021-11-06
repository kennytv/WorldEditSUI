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

import com.sk89q.worldedit.regions.Region;
import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import eu.kennytv.worldeditsui.compat.SimpleVector;
import eu.kennytv.worldeditsui.drawer.base.DrawedType;
import eu.kennytv.worldeditsui.drawer.base.DrawerBase;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class CuboidDrawer extends DrawerBase {

    //TODO: probably needs a complete rework
    CuboidDrawer(final WorldEditSUIPlugin plugin) {
        super(plugin);
    }

    @Override
    public void draw(final Player player, final Region region, final DrawedType drawedType) {
        if (!hasValidSize(player, region)) return;

        final double width = region.getWidth();
        final double length = region.getLength();
        final double height = region.getHeight();
        final SimpleVector minimumVector = plugin.getRegionHelper().getMinimumPoint(region);
        final Location minimumPoint = new Location(player.getWorld(), minimumVector.getX(), minimumVector.getY(), minimumVector.getZ());

        final double maxTicksX = width * settings.getParticlesPerBlock() - 1;
        final double maxTicksZ = length * settings.getParticlesPerBlock() - 1;
        double maxGridTicks = 0;
        double maxTopGridTicksX = 0;
        double maxTopGridTicksZ = 0;
        int gridSpaceX = 0;
        int gridSpaceZ = 0;
        int topGridSpace = 0;
        if (settings.hasAdvancedGrid(drawedType)) {
            gridSpaceX = settings.getParticlesPerBlock() * checkSpace(((int) ((width * height) / AREA_FACTOR) + 1));
            gridSpaceZ = settings.getParticlesPerBlock() * checkSpace(((int) ((length * height) / AREA_FACTOR) + 1));
            topGridSpace = settings.getParticlesPerBlock() * checkSpace(((int) ((width * length) / AREA_FACTOR) + 1));
            maxGridTicks = height * settings.getParticlesPerGridBlock(drawedType) - 1;
            maxTopGridTicksX = length * settings.getParticlesPerGridBlock(drawedType) - 1;
            maxTopGridTicksZ = width * settings.getParticlesPerGridBlock(drawedType) - 1;
        }

        drawLines(player, minimumPoint.clone(), gridSpaceX, topGridSpace, maxTicksX, maxGridTicks, maxTopGridTicksX, height, true, drawedType);
        drawLines(player, minimumPoint.clone().add(0, 0, length), gridSpaceX, 0, maxTicksX, maxGridTicks, 0, height, true, drawedType);
        drawLines(player, minimumPoint.clone(), gridSpaceZ, topGridSpace, maxTicksZ, maxGridTicks, maxTopGridTicksZ, height, false, drawedType);
        drawLines(player, minimumPoint.clone().add(width, 0, 0), gridSpaceZ, 0, maxTicksZ, maxGridTicks, 0, height, false, drawedType);
        drawPillarsAndGrid(player, minimumPoint, gridSpaceX, gridSpaceZ, height, width, length, drawedType);
    }

    private void drawLines(final Player player, final Location location, final int gridSpace, final int topGridSpace,
                           final double maxTicks, final double maxGridTicks, final double maxTopGridTicks,
                           final double height, final boolean x, final DrawedType drawedType) {
        // Lower row (with vertical grid)
        final double oldX = location.getX();
        final double oldZ = location.getZ();
        int blocks = 0;
        for (int i = 0; i < maxTicks; i++) {
            if (settings.hasAdvancedGrid(drawedType)) {
                if (blocks % gridSpace == 0 && i != 0) {
                    final Location clone = location.clone();
                    for (double j = 0; j < maxGridTicks; j++) {
                        clone.add(0, settings.getParticleGridSpace(drawedType), 0);
                        playEffect(clone, player, drawedType);
                    }
                }
                if (topGridSpace != 0 && blocks % topGridSpace == 0 && i != 0) {
                    tickGrid(player, location, maxTopGridTicks, x, drawedType);
                }
                blocks++;
            }

            if (x) {
                location.add(settings.getParticleSpace(), 0, 0);
            } else {
                location.add(0, 0, settings.getParticleSpace());
            }
            playEffect(location, player, drawedType);
        }

        // Upper row
        location.setX(oldX);
        location.setZ(oldZ);
        location.setY(location.getY() + height);
        blocks = 0;
        for (double i = 0; i < maxTicks; i++) {
            if (settings.hasAdvancedGrid(drawedType) && topGridSpace != 0 && blocks++ % topGridSpace == 0 && i != 0) {
                tickGrid(player, location, maxTopGridTicks, x, drawedType);
            }

            if (x) {
                location.add(settings.getParticleSpace(), 0, 0);
            } else {
                location.add(0, 0, settings.getParticleSpace());
            }
            playEffect(location, player, drawedType);
        }
    }

    private void tickGrid(final Player player, final Location location, final double maxTopGridTicks, final boolean x, final DrawedType drawedType) {
        final Location clone = location.clone();
        for (double j = 0; j < maxTopGridTicks; j++) {
            if (x)
                clone.add(0, 0, settings.getParticleGridSpace(drawedType));
            else
                clone.add(settings.getParticleGridSpace(drawedType), 0, 0);
            playEffect(clone, player, drawedType);
        }
    }

    private void drawPillarsAndGrid(final Player player, final Location minimum, final int gridSpaceX, final int gridSpaceZ,
                                    final double height, final double width, final double length, final DrawedType drawedType) {
        final boolean advancedGridEnabled = settings.hasAdvancedGrid(drawedType);

        final double x = minimum.getX();
        final double y = minimum.getY();
        final double z = minimum.getZ();
        final double gridSpace = advancedGridEnabled ? settings.getParticleGridSpace(drawedType) : 0;
        final double maxTicks = height * settings.getParticlesPerBlock();
        final double maxGridTicksX = advancedGridEnabled ? width * settings.getParticlesPerGridBlock(drawedType) - 1 : 0;
        final double maxGridTicksZ = advancedGridEnabled ? length * settings.getParticlesPerGridBlock(drawedType) - 1 : 0;
        setGrid(player, minimum, gridSpaceX, maxTicks, maxGridTicksX, gridSpace, 0, drawedType);
        minimum.setX(x + width);
        minimum.setY(y);
        minimum.setZ(z + length);
        setGrid(player, minimum, gridSpaceX, maxTicks, maxGridTicksX, -gridSpace, 0, drawedType);
        minimum.setX(x + width);
        minimum.setY(y);
        minimum.setZ(z);
        setGrid(player, minimum, gridSpaceZ, maxTicks, maxGridTicksZ, 0, gridSpace, drawedType);
        minimum.setX(x);
        minimum.setY(y);
        minimum.setZ(z + length);
        setGrid(player, minimum, gridSpaceZ, maxTicks, maxGridTicksZ, 0, -gridSpace, drawedType);
    }

    private void setGrid(final Player player, final Location location, final int gridSpace, final double maxTicks, final double maxGridTicks,
                         final double xAddition, final double zAddition, final DrawedType drawedType) {
        int blocks = 0;
        for (int i = 0; i < maxTicks; i++) {
            // Horizontal grid
            if (settings.hasAdvancedGrid(drawedType) && blocks++ % gridSpace == 0 && i != 0) {
                final Location clone = location.clone();
                for (double j = 0; j < maxGridTicks; j++) {
                    clone.add(xAddition, 0, zAddition);
                    playEffect(clone, player, drawedType);
                }
            }

            // Pillar
            location.add(0, settings.getParticleSpace(), 0);
            playEffect(location, player, drawedType);
        }
    }
}
