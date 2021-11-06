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

import eu.kennytv.worldeditsui.compat.SelectionType;
import eu.kennytv.worldeditsui.compat.SimpleVector;

import java.util.ArrayList;
import java.util.List;

public final class SelectionCache {

    private final List<SimpleVector> vectors = new ArrayList<>();
    private SelectionType selectionType = SelectionType.NONE;
    private SimpleVector minimum;
    private SimpleVector maximum;

    public List<SimpleVector> getVectors() {
        return vectors;
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(final SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public SimpleVector getMinimum() {
        return minimum;
    }

    public void setMinimum(final SimpleVector minimum) {
        this.minimum = minimum;
    }

    public SimpleVector getMaximum() {
        return maximum;
    }

    public void setMaximum(final SimpleVector maximum) {
        this.maximum = maximum;
    }
}
