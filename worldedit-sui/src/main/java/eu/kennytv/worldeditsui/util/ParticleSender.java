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

import java.util.List;
import org.bukkit.Location;

/**
 * Receivers of a selection draw batch, collected once per batch.
 */
public interface ParticleSender {

    ParticleSender EMPTY = new ParticleSender() {
        @Override
        public boolean hasViewers() {
            return false;
        }

        @Override
        public void play(final Location location) {
        }
    };

    default boolean hasViewers() {
        return true;
    }

    void play(Location location);

    static ParticleSender of(final ParticleData particle, final List<ParticleViewer> viewers) {
        if (viewers.isEmpty()) {
            return EMPTY;
        }
        if (viewers.size() == 1) {
            return new Single(particle, viewers.get(0));
        }
        return new Multi(particle, viewers);
    }



    final class Single implements ParticleSender {

        private final ParticleData particle;
        private final ParticleViewer viewer;

        Single(final ParticleData particle, final ParticleViewer viewer) {
            this.particle = particle;
            this.viewer = viewer;
        }

        @Override
        public void play(final Location location) {
            viewer.play(particle, location);
        }
    }

    final class Multi implements ParticleSender {

        private final ParticleData particle;
        private final ParticleViewer[] viewers;

        Multi(final ParticleData particle, final List<ParticleViewer> viewers) {
            this.particle = particle;
            this.viewers = viewers.toArray(new ParticleViewer[0]);
        }

        @Override
        public void play(final Location location) {
            for (final ParticleViewer viewer : viewers) {
                viewer.play(particle, location);
            }
        }
    }
}
