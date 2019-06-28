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

import eu.kennytv.worldeditcui.Settings;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UserManager {
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<UUID, Long> expireTimestamps = new HashMap<>();
    private final Settings settings;

    public UserManager(final Settings settings) {
        this.settings = settings;
    }

    public User getUser(final Player player) {
        return users.get(player.getUniqueId());
    }

    public void createUser(final Player player) {
        final boolean selection;
        final boolean clipboard;
        if (settings.hasPersistentToggles()) {
            final String uuid = player.getUniqueId().toString();
            selection = settings.getUserData().contains("selection." + uuid) ? settings.getUserData().getBoolean("selection." + uuid) : settings.showByDefault();
            clipboard = settings.getUserData().contains("clipboard." + uuid) ? settings.getUserData().getBoolean("clipboard." + uuid) : settings.showClipboardByDefault();
        } else {
            selection = settings.showByDefault();
            clipboard = settings.showClipboardByDefault();
        }
        users.put(player.getUniqueId(), new User(selection, clipboard));
    }

    public void deleteUser(final Player player) {
        expireTimestamps.remove(player.getUniqueId());
        final User remove = users.remove(player.getUniqueId());
        final SelectionCache cache = remove.getSelectionCache();
        if (cache != null)
            cache.getVectors().clear();
    }

    public Map<UUID, User> getUsers() {
        return users;
    }

    public Map<UUID, Long> getExpireTimestamps() {
        return expireTimestamps;
    }
}
