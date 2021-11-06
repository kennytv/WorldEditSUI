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

package eu.kennytv.worldeditsui.drawer.base;

import com.sk89q.worldedit.regions.Region;
import eu.kennytv.worldeditsui.Settings;
import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import eu.kennytv.worldeditsui.compat.SimpleVector;
import eu.kennytv.worldeditsui.user.SelectionCache;
import eu.kennytv.worldeditsui.user.User;
import eu.kennytv.worldeditsui.util.ParticleData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class DrawerBase implements Drawer {

    protected static final int MAX_GAP = 30;
    protected static final int AREA_FACTOR = 256;
    protected final WorldEditSUIPlugin plugin;
    protected final Settings settings;

    protected DrawerBase(final WorldEditSUIPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    protected boolean hasValidSize(final Player player, final Region region) {
        if (settings.getMaxSelectionSizeToDisplay() == 0) return true;
        return region.getArea() <= settings.getMaxSelectionSizeToDisplay()
                || (settings.useMaxSelectionSizeBypassPerm() && player.hasPermission("wesui.maxselectionsize.bypass"));
    }

    protected void playEffect(final Location location, final Player player) {
        playEffect(location, player, DrawedType.SELECTED);
    }

    protected void playEffect(final Location location, final Player player, final DrawedType drawedType) {
        if (settings.cacheLocations() && drawedType != DrawedType.CLIPBOARD) {
            final User user = plugin.getUserManager().getUser(player);
            if (user == null) return;

            final SelectionCache cache = user.getSelectionCache(drawedType);
            cache.getVectors().add(new SimpleVector(location.getX(), location.getY(), location.getZ()));
        }

        final ParticleData particle = settings.getParticle(drawedType);
        if (settings.sendParticlesToAll(drawedType)) {
            final ParticleData othersParticle = settings.getOthersParticle(drawedType);
            plugin.getParticleHelper().playEffectToAll(particle, othersParticle, location, settings.getParticleViewDistance(), player);
        } else {
            plugin.getParticleHelper().playEffect(particle, location, settings.getParticleViewDistance(), player);
        }
    }

    protected int checkSpace(final int gap) {
        return Math.min(gap, MAX_GAP);
    }
}
