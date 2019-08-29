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

package eu.kennytv.worldeditsui.drawer.base;

import eu.kennytv.worldeditsui.Settings;
import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import eu.kennytv.worldeditsui.compat.SimpleVector;
import eu.kennytv.worldeditsui.user.User;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public abstract class DrawerBase implements Drawer {

    protected static final int AREA_FACTOR = 256;
    protected final WorldEditSUIPlugin plugin;
    protected final Settings settings;

    protected DrawerBase(final WorldEditSUIPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    protected void playEffect(final Location location, final Player player) {
        playEffect(location, player, false);
    }

    protected void playEffect(final Location location, final Player player, final boolean copySelection) {
        if (settings.cacheLocations() && !copySelection) {
            final User user = plugin.getUserManager().getUser(player);
            if (user == null) return;
            user.getSelectionCache().getVectors().add(new SimpleVector(location.getX(), location.getY(), location.getZ()));
        }

        final Particle particle = copySelection ? settings.getClipboardParticle() : settings.getParticle();
        if (settings.sendParticlesToAll()) {
            final Particle othersParticle = copySelection ? settings.getOthersClipboardParticle() : settings.getOthersParticle();
            plugin.getParticleHelper().playEffectToAll(particle, othersParticle, location, 0, 1, settings.getParticleViewDistance(), player);
        } else {
            plugin.getParticleHelper().playEffect(particle, location, 0, 1, settings.getParticleViewDistance(), player);
        }
    }
}
