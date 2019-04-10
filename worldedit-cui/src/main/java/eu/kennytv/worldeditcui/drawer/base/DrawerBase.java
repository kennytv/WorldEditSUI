package eu.kennytv.worldeditcui.drawer.base;

import eu.kennytv.util.particlelib.ParticleEffectUtil;
import eu.kennytv.worldeditcui.Settings;
import eu.kennytv.worldeditcui.WorldEditCUIPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class DrawerBase implements Drawer {
    protected static final int AREA_FACTOR = 256;
    protected final WorldEditCUIPlugin plugin;
    protected final Settings settings;

    protected DrawerBase(final WorldEditCUIPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    protected void playEffect(final Location location, final Player player) {
        if (player != null)
            ParticleEffectUtil.playEffect(settings.getParticle(), location, 0, 1, 99, player);
        else
            ParticleEffectUtil.playEffect(settings.getParticle(), location, 0, 1, 99);
    }

    protected void playEffect(final Location location, final Player player, final boolean copySelection) {
        if (player != null)
            ParticleEffectUtil.playEffect(copySelection ? settings.getCopyParticle() : settings.getParticle(), location, 0, 1, 99, player);
        else
            ParticleEffectUtil.playEffect(copySelection ? settings.getCopyParticle() : settings.getParticle(), location, 0, 1, 99);
    }
}
