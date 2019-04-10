package eu.kennytv.worldeditcui.listener;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.event.platform.CommandEvent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import eu.kennytv.worldeditcui.WorldEditCUIPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public final class WESelectionListener implements Listener {
    private final WorldEditCUIPlugin plugin;
    private final Set<String> weCommands;
    private Material wand;

    public WESelectionListener(final WorldEditCUIPlugin plugin) {
        this.plugin = plugin;
        plugin.getWorldEditPlugin().getWorldEdit().getEventBus().register(this);
        weCommands = Sets.newHashSet("/pos1", "/pos2", "/chunk", "/hpos1", "/hpos2", "/expand", "/contract", "/shift", "/outset", "/inset",
                "/copy", "/cut", "/rotate", "/flip", "/clearclipboard");

        try {
            wand = plugin.getRegionHelper().getWand(plugin.getWorldEditPlugin());
        } catch (final Exception e) {
            // Fallback if the item cannot be read for some reason
            if (plugin.getSettings().getWandItem().isEmpty()) {
                plugin.getLogger().info("If you have set another item than the wooden axe as the WE wand, please write it into the WECUI config, for example:");
                plugin.getLogger().info("wand: WOODEN_SWORD");
                wand = Material.getMaterial("WOODEN_AXE");
                return;
            }

            final Material material = Material.getMaterial(plugin.getSettings().getWandItem());
            if (material == null) {
                plugin.getLogger().info("Unknown Material: " + plugin.getSettings().getWandItem());
                wand = Material.getMaterial("WOODEN_AXE");
            } else
                wand = material;
        }
    }

    @EventHandler
    public void playerInteract(final PlayerInteractEvent event) {
        if (!plugin.getSettings().isExpiryEnabled()) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final Player player = event.getPlayer();
        final ItemStack item = player.getItemInHand();
        if (item == null) return;
        if (wand != null) {
            if (wand != item.getType()) return;
        } else {
            final String type = item.getType().name();
            if (!type.endsWith("_AXE") || !type.contains("WOOD")) return;
        }
        plugin.getUserManager().getExpireTimestamps().put(player.getUniqueId(), System.currentTimeMillis() + plugin.getSettings().getExpiresAfterMillis());
    }

    @Subscribe
    public void worldEditCommand(final CommandEvent event) {
        if (!plugin.getSettings().isExpiryEnabled()) return;
        if (event.getArguments().length() < 3) return;

        final String command = event.getArguments().split(" ", 2)[0].toLowerCase();
        if (weCommands.stream().noneMatch(command::equals)) return;
        plugin.getUserManager().getExpireTimestamps().put(event.getActor().getUniqueId(), System.currentTimeMillis() + plugin.getSettings().getExpiresAfterMillis());
    }
}
