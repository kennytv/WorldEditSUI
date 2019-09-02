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

package eu.kennytv.worldeditsui.user;

import com.sk89q.worldedit.Vector;
import eu.kennytv.worldeditsui.util.SelectionType;

import java.util.ArrayList;
import java.util.List;

public final class SelectionCache {

    private final List<Vector> vectors = new ArrayList<>();
    private SelectionType selectionType = SelectionType.NONE;
    private Vector minimum;
    private Vector maximum;

    public List<Vector> getVectors() {
        return vectors;
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(final SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public Vector getMinimum() {
        return minimum;
    }

    public void setMinimum(final Vector minimum) {
        this.minimum = minimum;
    }

    public Vector getMaximum() {
        return maximum;
    }

    public void setMaximum(final Vector maximum) {
        this.maximum = maximum;
    }
}
