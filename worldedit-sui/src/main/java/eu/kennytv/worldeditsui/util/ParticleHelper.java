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
import eu.kennytv.worldeditsui.compat.Vector3D;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class ParticleHelper {

    // Drawers place some particles slightly outside of the region's bounds (e.g. ellipsoid radius offsets)
    private static final double BOUNDS_MARGIN = 5;
    private final Settings settings;

    public ParticleHelper(final Settings settings) {
        this.settings = settings;
    }

    public ParticleSender createSender(final ParticleData particle, @Nullable final ParticleData othersParticle,
                                       final Player origin, final Vector3D minimum, final Vector3D maximum) {
        // Collect viewers of a draw batch once; filter out players out-of-range.
        final int radiusSquared = particle.radiusSquared();
        if (othersParticle == null) {
            final Location location = origin.getLocation();
            if (!isNear(location, radiusSquared, minimum, maximum)) {
                return ParticleSender.EMPTY;
            }
            return new ParticleSender.Single(particle, new ParticleViewer(origin, location, particle));
        }

        final World world = origin.getWorld();
        final List<ParticleViewer> viewers = new ArrayList<>();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!world.equals(player.getWorld())) {
                continue;
            }

            final Location location = player.getLocation();
            if (!isNear(location, radiusSquared, minimum, maximum)) {
                continue;
            }

            final boolean originalPlayer = player.getUniqueId().equals(origin.getUniqueId());
            if (!originalPlayer && !canSeeOtherParticles(player)) {
                continue;
            }

            viewers.add(new ParticleViewer(player, location, originalPlayer ? particle : othersParticle));
        }
        return ParticleSender.of(particle, viewers);
    }

    private static boolean isNear(final Location location, final int radiusSquared, final Vector3D min, final Vector3D max) {
        final double dx = dist(location.getX(), min.getX() - BOUNDS_MARGIN, max.getX() + BOUNDS_MARGIN);
        final double dy = dist(location.getY(), min.getY() - BOUNDS_MARGIN, max.getY() + BOUNDS_MARGIN);
        final double dz = dist(location.getZ(), min.getZ() - BOUNDS_MARGIN, max.getZ() + BOUNDS_MARGIN);
        return dx * dx + dy * dy + dz * dz <= radiusSquared;
    }

    private static double dist(final double value, final double minimum, final double maximum) {
        if (value < minimum) return minimum - value;
        if (value > maximum) return value - maximum;
        return 0;
    }

    private boolean canSeeOtherParticles(final Player player) {
        return settings.getOtherParticlesPermission() == null || player.hasPermission(settings.getOtherParticlesPermission());
    }
}
