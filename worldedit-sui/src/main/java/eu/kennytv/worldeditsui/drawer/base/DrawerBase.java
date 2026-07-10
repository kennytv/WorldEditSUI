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
import eu.kennytv.worldeditsui.user.SelectionCache;
import eu.kennytv.worldeditsui.user.User;
import eu.kennytv.worldeditsui.util.ParticleData;
import eu.kennytv.worldeditsui.util.ParticleSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
        return settings.getMaxSelectionSizeToDisplay() == 0
            || plugin.getRegionHelper().getVolume(region) <= settings.getMaxSelectionSizeToDisplay()
            || (settings.useMaxSelectionSizeBypassPerm() && player.hasPermission("wesui.maxselectionsize.bypass"));
    }

    /**
     * Returns the context to draw with, or null if there is neither a cache to fill nor any player in range.
     *
     * @return the context to draw with, or null if there is neither a cache to fill nor any player in range
     */
    @Nullable
    protected DrawContext createContext(final Player player, final Region region, final DrawedType drawedType) {
        SelectionCache.Positions positions = null;
        if (settings.cacheLocations() && drawedType != DrawedType.CLIPBOARD) {
            final User user = plugin.getUserManager().getUser(player);
            if (user == null) {
                return null;
            }

            final SelectionCache cache = user.getSelectionCache(drawedType);
            if (cache != null) {
                positions = cache.getPositions();
            }
        }

        final ParticleData particle = settings.getParticle(drawedType);
        final ParticleData othersParticle = settings.sendParticlesToAll(drawedType) ? settings.getOthersParticle(drawedType) : null;
        final ParticleSender sender = plugin.getParticleHelper().createSender(
            particle,
            othersParticle,
            player,
            plugin.getRegionHelper().getMinimumPoint(region),
            plugin.getRegionHelper().getMaximumPoint(region)
        );
        return positions != null || sender.hasViewers() ? new DrawContext(sender, positions) : null;
    }

    protected int checkSpace(final int gap) {
        return Math.min(gap, MAX_GAP);
    }
}
