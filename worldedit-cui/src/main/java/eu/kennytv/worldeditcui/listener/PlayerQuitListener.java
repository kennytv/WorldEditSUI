package eu.kennytv.worldeditcui.listener;

import eu.kennytv.worldeditcui.user.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerQuitListener implements Listener {
    private final UserManager userManager;

    public PlayerQuitListener(final UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    public void playerQuit(final PlayerQuitEvent event) {
        userManager.deleteUser(event.getPlayer());
        userManager.getExpireTimestamps().remove(event.getPlayer().getUniqueId());
    }
}
