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

package eu.kennytv.worldeditsui.nms;

import eu.kennytv.worldeditsui.compat.nms.AbstractParticleHelper;
import eu.kennytv.worldeditsui.compat.nms.ViaParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public final class ParticleHelper extends AbstractParticleHelper {

    @Override
    public void playEffect(final ViaParticle particle, final Location location, final float offX, final float offY, final float offZ, final float speed, final int amount, final int radius, final Player player) {
        if (!location.getWorld().equals(player.getWorld())) return;

        final int radiusSquared = radius * radius;
        if (player.getLocation().distanceSquared(location) > radiusSquared) return;

        player.spawnParticle(Particle.valueOf(particle.name()), location, amount, offX, offY, offZ, speed);
    }

    @Override
    public void playEffectToAll(final ViaParticle particle, final ViaParticle othersParticle, final Location location, final float offX, final float offY, final float offZ, final float speed, final int amount, final int radius, final Player origin) {
        final int radiusSquared = radius * radius;
        final Particle bukkitParticle = Particle.valueOf(particle.name());
        final Particle othersBukkitParticle = particle == othersParticle ? bukkitParticle : Particle.valueOf(othersParticle.name());
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!location.getWorld().equals(player.getWorld())
                    || player.getLocation().distanceSquared(location) > radiusSquared) continue;

            final boolean originalPlayer = player.getUniqueId().equals(origin.getUniqueId());
            if (!originalPlayer && !canSeeOtherParticles(player)) continue;

            player.spawnParticle(originalPlayer ? bukkitParticle : othersBukkitParticle, location, amount, offX, offY, offZ, speed);
        }
    }
}