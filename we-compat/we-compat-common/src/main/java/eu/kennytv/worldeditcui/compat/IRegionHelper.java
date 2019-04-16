package eu.kennytv.worldeditcui.compat;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;

public interface IRegionHelper {

    SimpleVector getRadius(EllipsoidRegion region);

    SimpleVector getRadius(EllipsoidRegion region, double offX, double offY, double offZ);

    SimpleVector getRadius(CylinderRegion region, double offX, double offZ);

    SimpleVector getCenter(Region region);

    SimpleVector getCenter(Region region, double offX, double offY, double offZ);

    SimpleVector getMinimumPoint(Region region);

    SimpleVector getOrigin(Clipboard clipboard);

    Region shift(Region region, double x, double y, double z);

    Material getWand(WorldEditPlugin plugin);

    void iterate(Polygonal2DRegion region, VectorAction action);
}
