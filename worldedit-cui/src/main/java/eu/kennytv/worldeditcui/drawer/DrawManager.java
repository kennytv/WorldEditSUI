package eu.kennytv.worldeditcui.drawer;

import eu.kennytv.worldeditcui.WorldEditCUIPlugin;
import eu.kennytv.worldeditcui.drawer.base.Drawer;

import java.util.HashMap;
import java.util.Map;

public final class DrawManager {
    private final Map<String, Drawer> drawers = new HashMap<>();

    public DrawManager(final WorldEditCUIPlugin plugin) {
        drawers.put("cuboid", new CuboidDrawer(plugin));
        final EllipsoidDrawer ellipsoidDrawer = new EllipsoidDrawer(plugin);
        drawers.put("sphere", ellipsoidDrawer);
        drawers.put("ellipsoid", ellipsoidDrawer);
        drawers.put("Cylinder", new CylinderDrawer(plugin));
        drawers.put("2Dx1D polygon", new PolygonalDrawer(plugin));
        /*
        "2Dx1D polygon"
        "Convex Polyhedron"
        */
    }

    public Drawer getDrawer(final String selectorType) {
        return drawers.get(selectorType);
    }
}
