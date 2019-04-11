package eu.kennytv.worldeditcui.drawer;

import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import eu.kennytv.worldeditcui.WorldEditCUIPlugin;
import eu.kennytv.worldeditcui.drawer.base.DrawerBase;
import org.bukkit.entity.Player;

public final class PolygonalDrawer extends DrawerBase {

    PolygonalDrawer(final WorldEditCUIPlugin plugin) {
        super(plugin);
    }

    @Override
    public void draw(final Player player, final Region region) {
        final Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;
    }
}
