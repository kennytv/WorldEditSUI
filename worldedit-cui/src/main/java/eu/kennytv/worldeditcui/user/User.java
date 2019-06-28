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

package eu.kennytv.worldeditcui.user;

public final class User {
    private SelectionCache selectionCache;
    private boolean selectionShown;
    private boolean clipboardShown;

    User(final boolean selectionShown, final boolean clipboardShown) {
        this.selectionShown = selectionShown;
        this.clipboardShown = clipboardShown;
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

    public SelectionCache getSelectionCache() {
        return selectionCache;
    }

    public void setSelectionCache(final SelectionCache selectionCache) {
        this.selectionCache = selectionCache;
    }
}
