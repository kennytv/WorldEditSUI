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

package eu.kennytv.worldeditsui.util;

import java.util.HashMap;
import java.util.Map;

public enum SelectionType {

    CUBOID("cuboid"),
    SPHERE("sphere"),
    ELLIPSOID("ellipsoid"),
    CYLINDER("Cylinder"),
    POLYGON("2Dx1D polygon"),
    NONE(null);

    private static final Map<String, SelectionType> TYPES = new HashMap<>();
    private final String key;

    SelectionType(final String key) {
        this.key = key;
    }

    static {
        for (final SelectionType type : SelectionType.values()) {
            TYPES.put(type.key, type);
        }
    }

    public static SelectionType fromKey(final String key) {
        return TYPES.get(key);
    }
}
