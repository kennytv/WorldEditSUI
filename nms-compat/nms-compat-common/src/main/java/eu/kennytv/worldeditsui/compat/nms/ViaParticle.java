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

package eu.kennytv.worldeditsui.compat.nms;

import java.util.HashMap;
import java.util.Map;

public enum ViaParticle {

    EXPLOSION_NORMAL,
    EXPLOSION_LARGE,
    EXPLOSION_HUGE,
    FIREWORKS_SPARK,
    WATER_BUBBLE,
    WATER_SPLASH,
    WATER_WAKE,
    SUSPENDED,
    SUSPENDED_DEPTH,
    CRIT,
    CRIT_MAGIC,
    SMOKE_NORMAL,
    SMOKE_LARGE,
    SPELL,
    SPELL_INSTANT,
    SPELL_MOB,
    SPELL_MOB_AMBIENT,
    SPELL_WITCH,
    DRIP_WATER,
    DRIP_LAVA,
    VILLAGER_ANGRY,
    VILLAGER_HAPPY,
    TOWN_AURA,
    NOTE,
    PORTAL,
    ENCHANTMENT_TABLE,
    FLAME,
    LAVA,
    FOOTSTEP,
    CLOUD,
    @Deprecated REDSTONE,
    SNOWBALL,
    SNOW_SHOVEL,
    SLIME,
    HEART,
    BARRIER,
    @Deprecated ITEM_CRACK,
    @Deprecated BLOCK_CRACK,
    @Deprecated BLOCK_DUST,
    WATER_DROP,
    ITEM_TAKE,
    MOB_APPEARANCE,
    // 1.9
    DRAGON_BREATH,
    END_ROD,
    DAMAGE_INDICATOR,
    SWEEP_ATTACK,
    @Deprecated FALLING_DUST,
    TOTEM,
    SPIT,
    // 1.13
    SQUID_INK,
    BUBBLE_POP,
    CURRENT_DOWN,
    BUBBLE_COLUMN_UP,
    NAUTILUS,
    DOLPHIN,
    // 1.14
    SNEEZE,
    CAMPFIRE_COSY_SMOKE,
    CAMPFIRE_SIGNAL_SMOKE,
    COMPOSTER,
    FLASH,
    FALLING_LAVA,
    LANDING_LAVA,
    FALLING_WATER;

    private static final Map<String, ViaParticle> BY_NAME = new HashMap<>();

    static {
        for (final ViaParticle particle : ViaParticle.values()) {
            BY_NAME.put(particle.name(), particle);
        }
    }

    public static ViaParticle getByName(final String name) {
        return BY_NAME.get(name.toUpperCase());
    }
}
