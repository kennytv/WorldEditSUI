package eu.kennytv.worldeditsui.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public final class ParticleViewer {

    // The spawnParticle overload with the force parameter only exists on 1.20.6 and above
    private static final boolean FORCE_AVAILABLE = hasForceParameter();

    private final Player player;
    private final Location location;
    private final ParticleData particle;

    ParticleViewer(final Player player, final Location location, final ParticleData particle) {
        this.player = player;
        this.location = location;
        this.particle = particle;
    }

    public void play(final ParticleData particle, final Location location) {
        if (this.location.distanceSquared(location) > particle.radiusSquared()) {
            return;
        }

        if (FORCE_AVAILABLE) {
            // Force the particle, so that the client doesn't discard it beyond 32 blocks
            player.spawnParticle(this.particle.getParticle(), location, 1, particle.offX(), particle.offY(), particle.offZ(), particle.speed(), this.particle.getData(), true);
        } else {
            player.spawnParticle(this.particle.getParticle(), location, 1, particle.offX(), particle.offY(), particle.offZ(), particle.speed(), this.particle.getData());
        }
    }

    private static boolean hasForceParameter() {
        try {
            Player.class.getMethod("spawnParticle", Particle.class, Location.class, int.class, double.class, double.class, double.class, double.class, Object.class, boolean.class);
            return true;
        } catch (final NoSuchMethodException e) {
            return false;
        }
    }
}
