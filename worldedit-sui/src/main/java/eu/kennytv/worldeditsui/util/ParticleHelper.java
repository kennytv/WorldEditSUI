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

package eu.kennytv.worldeditsui.util;

import eu.kennytv.worldeditsui.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ParticleHelper {

    private final Settings settings;

    public ParticleHelper(final Settings settings) {
        this.settings = settings;
    }

    public void playEffect(final ParticleData particle, final Location location, final Player player) {
        if (!location.getWorld().equals(player.getWorld())) return;

        if (player.getLocation().distanceSquared(location) > particle.radiusSquared()) return;

        player.spawnParticle(particle.getParticle(), location, 1,
                particle.offX(), particle.offY(), particle.offZ(), particle.speed(), particle.getData());
    }

    public void playEffectToAll(final ParticleData particle, final ParticleData othersParticle, final Location location, final Player origin) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!location.getWorld().equals(player.getWorld())
                    || player.getLocation().distanceSquared(location) > particle.radiusSquared()) continue;

            final boolean originalPlayer = player.getUniqueId().equals(origin.getUniqueId());
            if (!originalPlayer && !canSeeOtherParticles(player)) continue;

            final ParticleData toSend = originalPlayer ? particle : othersParticle;
            player.spawnParticle(toSend.getParticle(), location, 1,
                    particle.offX(), particle.offY(), particle.offZ(), particle.speed(), toSend.getData());
        }
    }

    private boolean canSeeOtherParticles(final Player player) {
        return settings.getOtherParticlesPermission() == null || player.hasPermission(settings.getOtherParticlesPermission());
    }
}