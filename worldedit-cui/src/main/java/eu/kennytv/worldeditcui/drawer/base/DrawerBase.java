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
            ParticleEffectUtil.playEffect(settings.getParticle(), location, 0, 1, settings.getParticleViewDistance(), player);
        else
            ParticleEffectUtil.playEffect(settings.getParticle(), location, 0, 1, settings.getParticleViewDistance());
    }

    protected void playEffect(final Location location, final Player player, final boolean copySelection) {
        if (player != null)
            ParticleEffectUtil.playEffect(copySelection ? settings.getCopyParticle() : settings.getParticle(), location, 0, 1, settings.getParticleViewDistance(), player);
        else
            ParticleEffectUtil.playEffect(copySelection ? settings.getCopyParticle() : settings.getParticle(), location, 0, 1, settings.getParticleViewDistance());
    }
}
