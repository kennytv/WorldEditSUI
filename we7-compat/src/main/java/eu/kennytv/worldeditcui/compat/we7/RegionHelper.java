package eu.kennytv.worldeditcui.compat.we7;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import eu.kennytv.worldeditcui.compat.IRegionHelper;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public final class RegionHelper implements IRegionHelper {

    @Override
    public Vector getRadius(final EllipsoidRegion region) {
        final Vector3 radius = region.getRadius();
        return new Vector(radius.getX(), radius.getY(), radius.getZ());
    }

    @Override
    public Vector getRadius(final EllipsoidRegion region, final double offX, final double offY, final double offZ) {
        final Vector3 radius = region.getRadius();
        return new Vector(radius.getX() + offX, radius.getY() + offY, radius.getZ() + offZ);
    }

    @Override
    public Vector getRadius(final CylinderRegion region, final double offX, final double offZ) {
        final Vector2 radius = region.getRadius();
        return new Vector(radius.getX() + offX, region.getHeight(), radius.getZ() + offZ);
    }

    @Override
    public Vector getCenter(final Region region) {
        final Vector3 center = region.getCenter();
        return new Vector(center.getX(), center.getY(), center.getZ());
    }

    @Override
    public Vector getCenter(final Region region, final double offX, final double offY, final double offZ) {
        final Vector3 center = region.getCenter();
        return new Vector(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
    }

    @Override
    public Vector getMinimumPoint(final Region region) {
        final BlockVector3 minimum = region.getMinimumPoint();
        return new Vector(minimum.getX(), minimum.getY(), minimum.getZ());
    }

    @Override
    public Vector getOrigin(final Clipboard clipboard) {
        final BlockVector3 origin = clipboard.getOrigin();
        return new Vector(origin.getX(), origin.getY(), origin.getZ());
    }

    @Override
    public Region shift(final Region region, final double x, final double y, final double z) {
        final Region clone = region.clone();
        try {
            clone.shift(BlockVector3.at(x, y, z));
        } catch (final RegionOperationException ignored) {
        }
        return clone;
    }

    @Override
    public Material getWand(final WorldEditPlugin plugin) {
        return Material.getMaterial(plugin.getLocalConfiguration().wandItem.toUpperCase().replace("MINECRAFT:", ""));
    }
}
