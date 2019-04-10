package eu.kennytv.worldeditcui.compat.we6;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import eu.kennytv.worldeditcui.compat.IRegionHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class RegionHelper implements IRegionHelper {

    @Override
    public Vector getRadius(final EllipsoidRegion region) {
        final com.sk89q.worldedit.Vector radius = region.getRadius();
        return new Vector(radius.getX(), radius.getY(), radius.getZ());
    }

    @Override
    public Vector getRadius(final EllipsoidRegion region, final double offX, final double offY, final double offZ) {
        final com.sk89q.worldedit.Vector radius = region.getRadius();
        return new Vector(radius.getX() + offX, radius.getY() + offY, radius.getZ() + offZ);
    }

    @Override
    public Vector getRadius(final CylinderRegion region, final double offX, final double offZ) {
        final com.sk89q.worldedit.Vector2D radius = region.getRadius();
        return new Vector(radius.getX() + offX, region.getHeight(), radius.getZ() + offZ);
    }


    @Override
    public Vector getCenter(final Region region) {
        final com.sk89q.worldedit.Vector center = region.getCenter();
        return new Vector(center.getX(), center.getY(), center.getZ());
    }

    @Override
    public Vector getCenter(final Region region, final double offX, final double offY, final double offZ) {
        final com.sk89q.worldedit.Vector center = region.getCenter();
        return new Vector(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
    }

    @Override
    public Vector getMinimumPoint(final Region region) {
        final com.sk89q.worldedit.Vector minimum = region.getMinimumPoint();
        return new Vector(minimum.getX(), minimum.getY(), minimum.getZ());
    }

    @Override
    public Vector getOrigin(final Clipboard clipboard) {
        final com.sk89q.worldedit.Vector origin = clipboard.getOrigin();
        return new Vector(origin.getX(), origin.getY(), origin.getZ());
    }

    @Override
    public Region shift(final Region region, final double x, final double y, final double z) {
        try {
            region.shift(new com.sk89q.worldedit.Vector(x, y, z));
        } catch (final RegionOperationException ignored) {
        }
        return region;
    }

    @Override
    public Material getWand(final WorldEditPlugin plugin) {
        return new ItemStack(plugin.getLocalConfiguration().wandItem).getType();
    }
}
