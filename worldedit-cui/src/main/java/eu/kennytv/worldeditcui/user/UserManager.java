package eu.kennytv.worldeditcui.user;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UserManager {
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<UUID, Long> expireTimestamps = new HashMap<>();

    public User getUser(final Player player) {
        return users.get(player.getUniqueId());
    }

    public void createUser(final Player player) {
        users.put(player.getUniqueId(), new User());
    }

    public void deleteUser(final Player player) {
        users.remove(player.getUniqueId());
    }

    public Map<UUID, User> getUsers() {
        return users;
    }

    public Map<UUID, Long> getExpireTimestamps() {
        return expireTimestamps;
    }
}
