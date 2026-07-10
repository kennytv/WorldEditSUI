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
import eu.kennytv.worldeditsui.compat.Vector3D;
import eu.kennytv.worldeditsui.util.ParticleSender;
import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.World;

public final class SelectionCache {

    private Positions positions = new Positions();
    private SelectionType selectionType = SelectionType.NONE;
    private Vector3D min;
    private Vector3D max;
    private int shapeKey;

    public Positions getPositions() {
        return positions;
    }

    public void clear() {
        // Change reference, other threads might be using the old
        positions = new Positions();
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(final SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public Vector3D getMinimum() {
        return min;
    }

    public void setMinimum(final Vector3D minimum) {
        this.min = minimum;
    }

    public Vector3D getMaximum() {
        return max;
    }

    public void setMaximum(final Vector3D maximum) {
        this.max = maximum;
    }

    /**
     * @return additional compare value for regions whose shape can change without their bounding box changing
     */
    public int getShapeKey() {
        return shapeKey;
    }

    public void setShapeKey(final int shapeKey) {
        this.shapeKey = shapeKey;
    }

    public static final class Positions {

        private static final int TRIPLET_SIZE = 3;
        private double[] data = new double[192]; // stored as flat x/y/z triplets to avoid extra allocation
        private int size;

        public void add(final double x, final double y, final double z) {
            if (size + TRIPLET_SIZE > data.length) {
                data = Arrays.copyOf(data, data.length * 2);
            }
            data[size] = x;
            data[size + 1] = y;
            data[size + 2] = z;
            size += TRIPLET_SIZE;
        }

        public void play(final World world, final ParticleSender sender) {
            final Location location = new Location(world, 0, 0, 0);
            for (int i = 0, length = size; i < length; i += 3) {
                location.setX(data[i]);
                location.setY(data[i + 1]);
                location.setZ(data[i + 2]);
                sender.play(location);
            }
        }
    }
}
