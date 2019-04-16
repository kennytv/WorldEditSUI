package eu.kennytv.worldeditcui.compat.we7;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.*;
import eu.kennytv.worldeditcui.compat.IRegionHelper;
import eu.kennytv.worldeditcui.compat.SimpleVector;
import eu.kennytv.worldeditcui.compat.VectorAction;
import org.bukkit.Material;

public final class RegionHelper implements IRegionHelper {

    @Override
    public SimpleVector getRadius(final EllipsoidRegion region) {
        final Vector3 radius = region.getRadius();
        return new SimpleVector(radius.getX(), radius.getY(), radius.getZ());
    }

    @Override
    public SimpleVector getRadius(final EllipsoidRegion region, final double offX, final double offY, final double offZ) {
        final Vector3 radius = region.getRadius();
        return new SimpleVector(radius.getX() + offX, radius.getY() + offY, radius.getZ() + offZ);
    }

    @Override
    public SimpleVector getRadius(final CylinderRegion region, final double offX, final double offZ) {
        final Vector2 radius = region.getRadius();
        return new SimpleVector(radius.getX() + offX, region.getHeight(), radius.getZ() + offZ);
    }

    @Override
    public SimpleVector getCenter(final Region region) {
        final Vector3 center = region.getCenter();
        return new SimpleVector(center.getX(), center.getY(), center.getZ());
    }

    @Override
    public SimpleVector getCenter(final Region region, final double offX, final double offY, final double offZ) {
        final Vector3 center = region.getCenter();
        return new SimpleVector(center.getX() + offX, center.getY() + offY, center.getZ() + offZ);
    }

    @Override
    public SimpleVector getMinimumPoint(final Region region) {
        final BlockVector3 minimum = region.getMinimumPoint();
        return new SimpleVector(minimum.getX(), minimum.getY(), minimum.getZ());
    }

    @Override
    public SimpleVector getOrigin(final Clipboard clipboard) {
        final BlockVector3 origin = clipboard.getOrigin();
        return new SimpleVector(origin.getX(), origin.getY(), origin.getZ());
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

    @Override
    public void iterate(final Polygonal2DRegion region, final VectorAction action) {
        region.polygonize(-1).forEach(point -> action.act(point.getX(), 75, point.getZ()));
    }
}
