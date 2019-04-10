package eu.kennytv.worldeditcui.drawer.base;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;

public interface Drawer {

    /**
     * Recreates the given with particles.
     * If the given player object is null, the particles
     * will be sent to all players nearby.
     *
     * @param player player to send the particles to
     * @param region region to be displayed with particles
     */
    void draw(Player player, Region region);

    /**
     * Recreates the given with particles.
     * If the given player object is null, the particles
     * will be sent to all players nearby.
     *
     * @param player        player to send the particles to
     * @param region        region to be displayed with particles
     * @param copySelection if the region is a copied region
     * @see #draw(Player, Region)
     * @deprecated only implemented by the CuboidDrawer
     */
    @Deprecated
    default void draw(final Player player, final Region region, final boolean copySelection) {
        this.draw(player, region);
    }
}
