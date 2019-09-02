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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ParticleHelper {

    /**
     * Only players with this permission can see another player's particles.
     */
    private String permission;

    public void playEffect(final ParticleData particle, final Location location, final float speed, final int amount, final int radius, final Player player) {
        playEffect(particle, location, 0, 0, 0, speed, amount, radius, player);
    }

    public void playEffect(final ParticleData particle, final Location location, final float offX, final float offY, final float offZ, final float speed, final int amount, final int radius, final Player player) {
        if (!location.getWorld().equals(player.getWorld())) return;

        final int radiusSquared = radius * radius;
        if (player.getLocation().distanceSquared(location) > radiusSquared) return;

        player.spawnParticle(particle.getParticle(), location, amount, offX, offY, offZ, speed, particle.getData());
    }

    public void playEffectToAll(final ParticleData particle, final ParticleData othersParticle, final Location location, final float speed, final int amount, final int radius, final Player origin) {
        playEffectToAll(particle, othersParticle, location, 0, 0, 0, speed, amount, radius, origin);
    }

    public void playEffectToAll(final ParticleData particle, final ParticleData othersParticle, final Location location, final float offX, final float offY, final float offZ, final float speed, final int amount, final int radius, final Player origin) {
        final int radiusSquared = radius * radius;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!location.getWorld().equals(player.getWorld())
                    || player.getLocation().distanceSquared(location) > radiusSquared) continue;

            final boolean originalPlayer = player.getUniqueId().equals(origin.getUniqueId());
            if (!originalPlayer && !canSeeOtherParticles(player)) continue;

            final ParticleData toSend = originalPlayer ? particle : othersParticle;
            player.spawnParticle(toSend.getParticle(), location, amount, offX, offY, offZ, speed, toSend.getData());
        }
    }

    public void setPermission(final String permission) {
        this.permission = permission;
    }

    private boolean canSeeOtherParticles(final Player player) {
        return permission == null || player.hasPermission(permission);
    }
}