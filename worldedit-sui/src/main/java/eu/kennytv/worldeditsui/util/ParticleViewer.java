package eu.kennytv.worldeditsui.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ParticleViewer {

    private final Player player;
    private final Location location;
    private final ParticleData particle;

    ParticleViewer(final Player player, final Location location, final ParticleData particle) {
        this.player = player;
        this.location = location;
        this.particle = particle;
    }

    public void play(final ParticleData particle, final Location location) {
        if (this.location.distanceSquared(location) <= particle.radiusSquared()) {
            player.spawnParticle(this.particle.getParticle(), location, 1,
                particle.offX(), particle.offY(), particle.offZ(), particle.speed(), this.particle.getData());
        }
    }
}
