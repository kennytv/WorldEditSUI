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

import eu.kennytv.worldeditsui.user.SelectionCache;
import eu.kennytv.worldeditsui.util.ParticleSender;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Shared context of a single draw batch.
 */
public final class DrawContext {

    private final ParticleSender sender;
    private final SelectionCache.Positions positions;

    DrawContext(final ParticleSender sender, @Nullable final SelectionCache.Positions positions) {
        this.sender = sender;
        this.positions = positions;
    }

    public void playEffect(final Location location) {
        if (positions != null) {
            positions.add(location.getX(), location.getY(), location.getZ());
        }
        sender.play(location);
    }
}
