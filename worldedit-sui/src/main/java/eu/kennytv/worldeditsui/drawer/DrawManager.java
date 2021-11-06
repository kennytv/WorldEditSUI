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

package eu.kennytv.worldeditsui.drawer;

import eu.kennytv.worldeditsui.WorldEditSUIPlugin;
import eu.kennytv.worldeditsui.compat.SelectionType;
import eu.kennytv.worldeditsui.drawer.base.Drawer;

import java.util.HashMap;
import java.util.Map;

public final class DrawManager {

    private final Map<SelectionType, Drawer> drawers = new HashMap<>();

    public DrawManager(final WorldEditSUIPlugin plugin) {
        drawers.put(SelectionType.CUBOID, new CuboidDrawer(plugin));
        final EllipsoidDrawer ellipsoidDrawer = new EllipsoidDrawer(plugin);
        drawers.put(SelectionType.SPHERE, ellipsoidDrawer);
        drawers.put(SelectionType.ELLIPSOID, ellipsoidDrawer);
        drawers.put(SelectionType.CYLINDER, new CylinderDrawer(plugin));
        drawers.put(SelectionType.POLYGON, new PolygonalDrawer(plugin));
        //TODO "Convex Polyhedron"?
        //TODO FAWE regions?
    }

    public Drawer getDrawer(final SelectionType selectionType) {
        return drawers.get(selectionType);
    }
}
