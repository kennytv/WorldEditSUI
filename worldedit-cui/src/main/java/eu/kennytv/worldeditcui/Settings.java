package eu.kennytv.worldeditcui;

import eu.kennytv.util.particlelib.ViaParticle;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class Settings {
    private final WorldEditCUIPlugin plugin;
    private String permission;
    private String wandItem;
    private double particleSpace;
    private double particleGridSpace;
    private int particlesPerBlock;
    private int particlesPerGridBlock;
    private int particleSendIntervall;
    private long expiresAfterMillis;
    private boolean expiryEnabled;
    private boolean expireMessage;
    private boolean advancedGrid;
    private boolean updateChecks;
    private boolean sendParticlesToAll;
    private ViaParticle particle;
    private ViaParticle copyParticle;
    private ViaParticle otherParticle;

    Settings(final WorldEditCUIPlugin plugin) {
        this.plugin = plugin;
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        loadSettings();
    }

    public void loadSettings() {
        final File file = new File(plugin.getDataFolder(), "config.yml");
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        wandItem = config.getString("wand", "").toUpperCase().replace("MINECRAFT:", "");

        particle = loadParticle(config, "particle", ViaParticle.FLAME);
        copyParticle = loadParticle(config, "copy-region-particle", ViaParticle.VILLAGER_HAPPY);
        otherParticle = loadParticle(config, "other-player-particle", ViaParticle.FLAME);

        particlesPerBlock = config.getInt("particles-per-block", 4);
        if (particlesPerBlock < 0.5 || particlesPerBlock > 5) {
            plugin.getLogger().warning("The value particles-per-block has to be set between 1 and 5!");
            plugin.getLogger().warning("Switched to default particles-per-block: 4");
            this.particlesPerBlock = 4;
            this.particleSpace = 0.25;
        } else {
            this.particleSpace = 1D / particlesPerBlock;
        }

        advancedGrid = config.getBoolean("advanced-grid.enabled", false);
        if (advancedGrid) {
            final int particlesPerGridBlock = config.getInt("advanced-grid.particles-per-block", 2);
            if (particlesPerGridBlock < 1 || particlesPerGridBlock > 5) {
                plugin.getLogger().warning("The value advanced-grid.particles-per-block has to be set between 2 and 5!");
                plugin.getLogger().warning("Switched to default advanced-grid.particles-per-block: 2");
                this.particlesPerGridBlock = 2;
                this.particleGridSpace = 0.5;
            } else {
                this.particlesPerGridBlock = particlesPerGridBlock;
                this.particleGridSpace = 1D / particlesPerGridBlock;
            }
        }

        particleSendIntervall = config.getInt("particle-send-intervall", 12);
        if (particleSendIntervall < 5 || particleSendIntervall > 200) {
            plugin.getLogger().warning("The particle-send-intervall has to be set between 5 and 200!");
            plugin.getLogger().warning("Switched to default particle-send-intervall: 12");
            this.particleSendIntervall = 12;
        }

        final String perm = config.getString("permission", "");
        permission = perm.equalsIgnoreCase("none") ? "" : perm;
        updateChecks = config.getBoolean("update-checks", true);
        sendParticlesToAll = config.getBoolean("send-particles-to-all", false);

        expiryEnabled = config.getBoolean("particle-expiry.enabled", false);
        if (expiryEnabled) {
            expireMessage = config.getBoolean("particle-expiry.expire-message", true);
            expiresAfterMillis = config.getInt("particle-expiry.expires-after-seconds", 120);
            if (expiresAfterMillis <= 0) {
                plugin.getLogger().warning("The expires-after-seconds has to be set higher than 0!");
                plugin.getLogger().warning("Switched to default expires-after-seconds: 180");
                expiresAfterMillis = 180_000;
            } else {
                expiresAfterMillis *= 1000;
            }
        }
    }

    private ViaParticle loadParticle(final YamlConfiguration config, final String s, final ViaParticle defaultParticle) {
        final String particleName = config.getString(s, defaultParticle.name());
        final ViaParticle particle = ViaParticle.getByName(particleName);
        if (particle == null) {
            plugin.getLogger().warning("Unknown particle for " + s + ": " + particleName.toUpperCase());
            plugin.getLogger().warning("Switched to default particle: " + defaultParticle);
            return defaultParticle;
        }
        return particle;
    }

    public String getPermission() {
        return permission;
    }

    public double getParticleSpace() {
        return particleSpace;
    }

    public double getParticleGridSpace() {
        return particleGridSpace;
    }

    public int getParticlesPerBlock() {
        return particlesPerBlock;
    }

    public int getParticlesPerGridBlock() {
        return particlesPerGridBlock;
    }

    public int getParticleSendIntervall() {
        return particleSendIntervall;
    }

    public long getExpiresAfterMillis() {
        return expiresAfterMillis;
    }

    public boolean isExpiryEnabled() {
        return expiryEnabled;
    }

    public boolean hasExpireMessage() {
        return expireMessage;
    }

    public boolean hasAdvancedGrid() {
        return advancedGrid;
    }

    public boolean hasUpdateChecks() {
        return updateChecks;
    }

    public boolean sendParticlesToAll() {
        return sendParticlesToAll;
    }

    public ViaParticle getParticle() {
        return particle;
    }

    public ViaParticle getCopyParticle() {
        return copyParticle;
    }

    public ViaParticle getOtherParticle() {
        return otherParticle;
    }

    public String getWandItem() {
        return wandItem;
    }
}
