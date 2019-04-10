package eu.kennytv.worldeditcui.listener;

import eu.kennytv.worldeditcui.WorldEditCUIPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerJoinListener implements Listener {
    private final Set<UUID> notified = new HashSet<>();
    private final WorldEditCUIPlugin plugin;
    private String newestVersion;

    public PlayerJoinListener(final WorldEditCUIPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        plugin.getUserManager().createUser(player);
        if (!plugin.getSettings().hasUpdateChecks()) return;
        if (!player.hasPermission("worldeditcui.admin")) return;
        if (notified.contains(player.getUniqueId())) return;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!updateAvailable()) return;

            player.sendMessage(plugin.getPrefix() + "§cThere is a newer version available: §aVersion " + newestVersion + "§c, you're on §a" + plugin.getDescription().getVersion());
            notified.add(player.getUniqueId());

            try {
                final TextComponent tc1 = new TextComponent(TextComponent.fromLegacyText(plugin.getPrefix()));
                final TextComponent tc2 = new TextComponent(TextComponent.fromLegacyText("§cDownload it at: §6https://www.spigotmc.org/resources/worldeditcui.60726/"));
                final TextComponent click = new TextComponent(TextComponent.fromLegacyText(" §7§l§o(CLICK ME)"));
                click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/worldeditcui.60726/"));
                click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aDownload the latest version").create()));
                tc1.addExtra(tc2);
                tc1.addExtra(click);

                player.spigot().sendMessage(tc1);
            } catch (final Exception e) {
                player.sendMessage(plugin.getPrefix() + "§cDownload it at: §6https://www.spigotmc.org/resources/worldeditcui.60726/");
            }
        });

    }

    private boolean updateAvailable() {
        try {
            final HttpURLConnection c = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=60726").openConnection();
            final String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z -]", "");

            final boolean available = !newVersion.equals(plugin.getDescription().getVersion());
            if (available)
                newestVersion = newVersion;

            return available;
        } catch (final Exception ignored) {
            return false;
        }
    }
}
