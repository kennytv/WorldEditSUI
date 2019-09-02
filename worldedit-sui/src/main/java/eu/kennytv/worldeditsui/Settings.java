/*
 * WorldEditSUI - https://git.io/wesui
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

package eu.kennytv.worldeditsui;

import eu.kennytv.worldeditsui.util.ParticleData;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class Settings {

    private final WorldEditSUIPlugin plugin;

    private YamlConfiguration userData;
    private YamlConfiguration language;
    private boolean changedUserData;

    private String permission;
    private String otherParticlesPermission;
    private String wandItem; // optional value, exists for fallback/legacy reasons
    private double particleSpace;
    private int particlesPerBlock;
    private int particleSendInterval;
    private int particleViewDistance;
    private long expiresAfterMillis;
    private boolean cacheLocations;
    private boolean expiryEnabled;
    private boolean expireMessage;
    private boolean updateChecks;
    private boolean sendParticlesToAll;
    private boolean persistentToggles;
    private boolean showByDefault;
    private boolean showClipboardByDefault;
    private ParticleData particle;
    private ParticleData clipboardParticle;
    private ParticleData othersParticle;
    private ParticleData othersClipboardParticle;
    private GridSettings advancedGrid;
    private GridSettings advancedClipboardGrid;

    Settings(final WorldEditSUIPlugin plugin) {
        this.plugin = plugin;
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        loadSettings();
        loadLanguageFile();
    }

    public void loadSettings() {
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        wandItem = config.getString("wand", "").toUpperCase().replace("MINECRAFT:", "");

        particle = loadParticle(config, "particle", Particle.FLAME);
        clipboardParticle = loadParticle(config, "clipboard-particle", Particle.VILLAGER_HAPPY);

        final ConfigurationSection sendToAllSection = config.getConfigurationSection("send-particles-to-all");
        sendParticlesToAll = sendToAllSection.getBoolean("enabled");
        otherParticlesPermission = sendToAllSection.getString("view-others-particles-perm", "");
        if (otherParticlesPermission.isEmpty() || otherParticlesPermission.equalsIgnoreCase("none")) {
            otherParticlesPermission = null;
        }
        plugin.getParticleHelper().setPermission(otherParticlesPermission);
        othersParticle = loadParticle(sendToAllSection, "others-particle", Particle.FLAME);
        othersClipboardParticle = loadParticle(sendToAllSection, "others-clipboard-particle", Particle.VILLAGER_HAPPY);

        particlesPerBlock = config.getInt("particles-per-block", 4);
        if (particlesPerBlock < 0.5 || particlesPerBlock > 5) {
            plugin.getLogger().warning("The value particles-per-block has to be set between 1 and 5!");
            plugin.getLogger().warning("Switched to default particles-per-block: 4");
            this.particlesPerBlock = 4;
            this.particleSpace = 0.25;
        } else {
            this.particleSpace = 1D / particlesPerBlock;
        }

        final ConfigurationSection gridSection = config.getConfigurationSection("advanced-grid");
        advancedGrid = gridSection.getBoolean("enabled", false) ? new GridSettings(plugin, gridSection) : null;

        final ConfigurationSection clipboardGridSection = config.getConfigurationSection("advanced-clipboard-grid");
        advancedClipboardGrid = clipboardGridSection != null && clipboardGridSection.getBoolean("enabled", false) ?
                new GridSettings(plugin, clipboardGridSection) : null;

        particleSendInterval = config.getInt("particle-send-interval", 12);
        if (particleSendInterval < 5 || particleSendInterval > 200) {
            plugin.getLogger().warning("The particle-send-interval has to be set between 5 and 200!");
            plugin.getLogger().warning("Switched to default particle-send-interval: 12");
            this.particleSendInterval = 12;
        }

        permission = config.getString("permission", "");
        if (permission.isEmpty() || permission.equalsIgnoreCase("none")) {
            permission = null;
        }

        cacheLocations = config.getBoolean("cache-calculated-positions", true);
        updateChecks = config.getBoolean("update-checks", true);
        persistentToggles = config.getBoolean("persistent-toggles");
        showByDefault = config.getBoolean("show-selection-by-default", true);
        showClipboardByDefault = config.getBoolean("show-clipboard-by-default");

        particleViewDistance = config.getInt("particle-viewdistance", 99);
        if (particleViewDistance < 1 || particleViewDistance > 500) {
            plugin.getLogger().warning("To punish you for your deeds of setting the particle viewdistance to an astonishing "
                    + particleViewDistance + ", it has been set to 2 blocks.");
            plugin.getLogger().warning("Also, this puppy just died.\n"
                    + "      __\n" +
                    " (___()'`;\n" +
                    " /,    /`\n" +
                    " \\\\\"--\\\\");
            plugin.getLogger().warning("Is this what you wanted?");
            particleViewDistance = 2;
        }

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

        if (persistentToggles) {
            final File file = new File(plugin.getDataFolder(), "userdata.yml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            userData = YamlConfiguration.loadConfiguration(file);
        }
    }

    public void loadLanguageFile() {
        final File file = new File(plugin.getDataFolder(), "language.yml");
        if (!file.exists()) {
            try (final InputStream in = plugin.getResource("language.yml")) {
                Files.copy(in, file.toPath());
            } catch (final IOException e) {
                throw new RuntimeException("Unable to create language.yml file for WorldEditSUI!", e);
            }
        }
        try {
            language = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ParticleData loadParticle(final ConfigurationSection section, final String s, final Particle defaultParticle) {
        final String particleName = section.getString(s, defaultParticle.name()).toUpperCase().replace("MINECRAFT:", "");
        final Particle particle;
        try {
            particle = Particle.valueOf(particleName);
        } catch (final Exception e) {
            plugin.getLogger().warning("Unknown particle for " + s + ": " + particleName.toUpperCase());
            plugin.getLogger().warning("Switched to default particle: " + defaultParticle);
            return new ParticleData(defaultParticle, null);
        }

        try {
            final Object data = ParticleData.getExtraData(particle, section.getConfigurationSection(s + "-data"));
            return new ParticleData(particle, data);
        } catch (final Exception e) {
            plugin.getLogger().warning("Error loading particle data of " + particleName + " - Missing data? You may read up on how to correctly set its data at the bottom of the plugin's Spigot page.");
            plugin.getLogger().warning("Falling back to default particle: " + defaultParticle);
            e.printStackTrace();
            return new ParticleData(particle, null);
        }
    }

    public void saveData() {
        if (userData == null || !changedUserData) return;
        plugin.getLogger().info("Saving userdata...");
        try {
            userData.save(new File(plugin.getDataFolder(), "userdata.yml"));
            changedUserData = false;
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserData(final String path, final boolean value) {
        userData.set(path, value);
        changedUserData = true;
    }

    public String getMessage(final String path) {
        String s = language.getString(path);
        if (s == null) {
            plugin.getLogger().warning("The language file is missing the following string: " + path);
            return "empty";
        }
        if (s.contains("%prefix%"))
            s = s.replace("%prefix%", language.getString("prefix", ""));
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String getPermission() {
        return permission;
    }

    // getter is unused since the perm is directly given to the particle helper class
    public String getOtherParticlesPermission() {
        return otherParticlesPermission;
    }

    public boolean hasAdvancedGrid() {
        return advancedGrid != null;
    }

    public boolean hasAdvancedGrid(final boolean clipboardSelection) {
        return clipboardSelection ? advancedClipboardGrid != null : advancedGrid != null;
    }

    /**
     * @see #hasAdvancedGrid()
     */
    public double getParticleGridSpace() {
        return advancedGrid.getParticleGridSpace();
    }

    /**
     * @see #hasAdvancedGrid(boolean)
     */
    public double getParticleGridSpace(final boolean clipboardSelection) {
        return clipboardSelection ? advancedClipboardGrid.getParticleGridSpace() : advancedGrid.getParticleGridSpace();
    }

    /**
     * @see #hasAdvancedGrid()
     */
    public int getParticlesPerGridBlock() {
        return advancedGrid.getParticlesPerGridBlock();
    }

    /**
     * @see #hasAdvancedGrid(boolean)
     */
    public int getParticlesPerGridBlock(final boolean clipboardSelection) {
        return clipboardSelection ? advancedClipboardGrid.getParticlesPerGridBlock() : advancedGrid.getParticlesPerGridBlock();
    }

    public double getParticleSpace() {
        return particleSpace;
    }

    public int getParticlesPerBlock() {
        return particlesPerBlock;
    }

    public int getParticleSendInterval() {
        return particleSendInterval;
    }

    public int getParticleViewDistance() {
        return particleViewDistance;
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

    public boolean hasUpdateChecks() {
        return updateChecks;
    }

    public boolean sendParticlesToAll() {
        return sendParticlesToAll;
    }

    public boolean hasPersistentToggles() {
        return persistentToggles;
    }

    public boolean showByDefault() {
        return showByDefault;
    }

    public boolean showClipboardByDefault() {
        return showClipboardByDefault;
    }

    public boolean cacheLocations() {
        return cacheLocations;
    }

    public ParticleData getParticle() {
        return particle;
    }

    public ParticleData getClipboardParticle() {
        return clipboardParticle;
    }

    public ParticleData getOthersParticle() {
        return othersParticle;
    }

    public ParticleData getOthersClipboardParticle() {
        return othersClipboardParticle;
    }

    public String getWandItem() {
        return wandItem;
    }

    public YamlConfiguration getUserData() {
        return userData;
    }

    public static final class GridSettings {

        private final int particlesPerGridBlock;
        private final double particleGridSpace;

        private GridSettings(final WorldEditSUIPlugin plugin, final ConfigurationSection section) {
            final int particlesPerGridBlock = section.getInt("particles-per-block", 2);
            if (particlesPerGridBlock < 1 || particlesPerGridBlock > 5) {
                plugin.getLogger().warning("The value particles-per-block in " + section.getCurrentPath() + " has to be set between 2 and 5!");
                plugin.getLogger().warning("Switched to default particles-per-block: 2");
                this.particlesPerGridBlock = 2;
                particleGridSpace = 0.5;
            } else {
                this.particlesPerGridBlock = particlesPerGridBlock;
                particleGridSpace = 1D / this.particlesPerGridBlock;
            }
        }

        public int getParticlesPerGridBlock() {
            return particlesPerGridBlock;
        }

        public double getParticleGridSpace() {
            return particleGridSpace;
        }
    }
}
