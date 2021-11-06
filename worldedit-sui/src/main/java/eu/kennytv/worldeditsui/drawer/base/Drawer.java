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

package eu.kennytv.worldeditsui.drawer.base;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;

public interface Drawer {

    /**
     * Recreates the given with particles.
     * If the given player object is null, the particles
     * will be sent to all players nearby.
     *
     * @param player     player to send the particles to, may be null when sent to all players
     * @param region     region to be displayed with particles
     * @param drawedType drawed type
     */
    void draw(Player player, Region region, DrawedType drawedType);

    /**
     * Recreates the given with particles.
     * If the given player object is null, the particles
     * will be sent to all players nearby.
     *
     * @param player player to send the particles to, may be null when sent to all players
     * @param region region to be displayed with particles
     */
    default void draw(final Player player, final Region region) {
        draw(player, region, DrawedType.SELECTED);
    }
}
