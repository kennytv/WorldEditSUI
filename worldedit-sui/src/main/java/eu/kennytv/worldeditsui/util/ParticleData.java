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

package eu.kennytv.worldeditsui.util;

import com.google.common.base.Preconditions;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public final class ParticleData {

    private final Particle particle;
    private final Object data;

    public ParticleData(final Particle particle, final Object data) {
        this.particle = particle;
        this.data = data;
    }

    public Particle getParticle() {
        return particle;
    }

    public Object getData() {
        return data;
    }

    //TODO proper error handling/warnings
    public static Object getExtraData(final Particle particle, final ConfigurationSection section) throws Exception {
        final Class<?> dataType = particle.getDataType();
        if (dataType == null || dataType == Void.class) return null;

        Preconditions.checkArgument(section != null, "The data section is missing!");

        final String name = dataType.getSimpleName();
        // they might exist, they might not ¯\_(ツ)_/¯
        // also it's only executed when (re)loading the config, so I think I might just get away with this method (prolly not worth doing extra modules for this)
        if (name.equals("DustOptions")) {
            final float size = (float) section.getDouble("size");
            final Color color = section.contains("rgb") ? Color.fromRGB(section.getInt("rgb"))
                    : Color.fromRGB(section.getInt("r"), section.getInt("g"), section.getInt("b"));
            return new Particle.DustOptions(color, size);
        } else if (name.equals("BlockData")) {
            final Material material = Material.getMaterial(section.getString("material"));
            Preconditions.checkArgument(material.isBlock(), "This particle requires a block material for its data.");
            return material.createBlockData();
        } else if (dataType == ItemStack.class) {
            return new ItemStack(Material.getMaterial(section.getString("material")));
        } else if (name.equals("MaterialData")) { // use the name in case of its removal
            return new MaterialData(Material.getMaterial(section.getString("material")), (byte) section.getInt("data"));
        } else if (dataType == Color.class) {
            return section.contains("rgb") ? Color.fromRGB(section.getInt("rgb"))
                    : Color.fromRGB(section.getInt("r"), section.getInt("g"), section.getInt("b"));
        }

        throw new IllegalArgumentException("The datatype " + name + " is not yet supported by the plugin - please report this error!");
    }
}
