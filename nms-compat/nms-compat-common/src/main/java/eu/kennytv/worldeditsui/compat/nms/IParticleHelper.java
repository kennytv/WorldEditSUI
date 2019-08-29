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

package eu.kennytv.worldeditsui.compat.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IParticleHelper {

    void playEffect(ViaParticle particle, Location location, float offX, float offY, float offZ, float speed, int amount, int radius, Player player);

    default void playEffect(final ViaParticle particle, final Location location, final float speed, final int amount, final int radius, final Player player) {
        playEffect(particle, location, 0, 0, 0, speed, amount, radius, player);
    }

    void playEffectToAll(ViaParticle particle, ViaParticle othersParticle, Location location, float offX, float offY, float offZ, float speed, int amount, int radius, Player origin);

    default void playEffectToAll(final ViaParticle particle, final ViaParticle othersParticle, final Location location, final float speed, final int amount, final int radius, final Player origin) {
        playEffectToAll(particle, othersParticle, location, 0, 0, 0, speed, amount, radius, origin);
    }

    void setPermission(String permission);
}
