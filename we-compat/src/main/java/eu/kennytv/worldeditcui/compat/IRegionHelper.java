package eu.kennytv.worldeditcui.compat;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public interface IRegionHelper {

    Vector getRadius(EllipsoidRegion region);

    Vector getRadius(EllipsoidRegion region, double offX, double offY, double offZ);

    Vector getRadius(CylinderRegion region, double offX, double offZ);

    Vector getCenter(Region region);

    Vector getCenter(Region region, double offX, double offY, double offZ);

    Vector getMinimumPoint(Region region);

    Vector getOrigin(Clipboard clipboard);

    Region shift(Region region, double x, double y, double z);

    Material getWand(WorldEditPlugin plugin);
}
