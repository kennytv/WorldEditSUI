package eu.kennytv.worldeditcui.compat;

public final class SimpleVector {
    private final double x;
    private final double y;
    private final double z;

    public SimpleVector(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
