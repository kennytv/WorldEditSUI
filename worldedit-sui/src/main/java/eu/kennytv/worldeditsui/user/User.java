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

package eu.kennytv.worldeditsui.user;

import eu.kennytv.worldeditsui.compat.ProtectedRegionWrapper;
import eu.kennytv.worldeditsui.drawer.base.DrawedType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class User {

    private final UUID uuid;
    private SelectionCache selectionCache;
    private SelectionCache selectedWGRegionCache;
    private ProtectedRegionWrapper selectedWGRegion;
    private boolean selectionShown;
    private boolean clipboardShown;
    private long expireTimestamp = -1;

    User(final UUID uuid, final boolean selectionShown, final boolean clipboardShown) {
        this.uuid = uuid;
        this.selectionShown = selectionShown;
        this.clipboardShown = clipboardShown;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isSelectionShown() {
        return selectionShown;
    }

    public void setSelectionShown(final boolean selectionShown) {
        this.selectionShown = selectionShown;
    }

    public boolean isClipboardShown() {
        return clipboardShown;
    }

    public void setClipboardShown(final boolean clipboardShown) {
        this.clipboardShown = clipboardShown;
    }

    /**
     * @return time at which the selection will expire, 0 if no longer shown, -1 if disabled
     */
    public long getExpireTimestamp() {
        return expireTimestamp;
    }

    public void setExpireTimestamp(final long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }

    @Nullable
    public SelectionCache getSelectionCache() {
        return selectionCache;
    }

    @Nullable
    public SelectionCache getSelectionCache(final DrawedType drawedType) {
        switch (drawedType) {
            case SELECTED:
                return selectionCache;
            case WG_REGION:
                return selectedWGRegionCache;
            default:
                throw new IllegalArgumentException("Only allowed types are SELECTED and WG_REGION");
        }
    }

    public void setSelectionCache(final DrawedType drawedType, @Nullable final SelectionCache selectionCache) {
        switch (drawedType) {
            case SELECTED:
                this.selectionCache = selectionCache;
                break;
            case WG_REGION:
                this.selectedWGRegionCache = selectionCache;
                break;
            default:
                throw new IllegalArgumentException("Only allowed types are SELECTED and WG_REGION");
        }
    }

    @Nullable
    public ProtectedRegionWrapper getSelectedWGRegion() {
        return selectedWGRegion;
    }

    public void setSelectedWGRegion(@Nullable final ProtectedRegionWrapper selectedWGRegion) {
        this.selectedWGRegion = selectedWGRegion;
    }

    public void clearCaches() {
        if (selectionCache != null) {
            selectionCache.getVectors().clear();
            selectionCache = null;
        }
        if (selectedWGRegionCache != null) {
            selectedWGRegionCache.getVectors().clear();
            selectedWGRegionCache = null;
        }
    }

    public void clearCache(final DrawedType drawedType) {
        final SelectionCache oldCache = getSelectionCache(drawedType);
        if (oldCache != null) {
            oldCache.getVectors().clear();
            setSelectionCache(drawedType, null);
        }
    }
}
