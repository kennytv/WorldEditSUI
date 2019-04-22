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

package eu.kennytv.worldeditcui.drawer.base;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;

public interface Drawer {

    /**
     * Recreates the given with particles.
     * If the given player object is null, the particles
     * will be sent to all players nearby.
     *
     * @param player player to send the particles to
     * @param region region to be displayed with particles
     */
    void draw(Player player, Region region);

    /**
     * Recreates the given with particles.
     * If the given player object is null, the particles
     * will be sent to all players nearby.
     *
     * @param player        player to send the particles to
     * @param region        region to be displayed with particles
     * @param copySelection if the region is a copied region
     * @see #draw(Player, Region)
     * @deprecated only implemented by the CuboidDrawer
     */
    @Deprecated
    default void draw(final Player player, final Region region, final boolean copySelection) {
        this.draw(player, region);
    }
}
