package uk.co.n3fs.sasha.api.type;

public class LocationRange {

    private final String world;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    private LocationRange(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getZ1() {
        return z1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getZ2() {
        return z2;
    }

    public static LocationRange radius(Location base, int radius) {
        return new LocationRange(
            base.getWorld(),
            base.getX() - radius, base.getY() - radius, base.getZ() - radius,
            base.getX() + radius, base.getY() + radius, base.getZ() + radius
        );
    }

    public static LocationRange delta(Location base, int dx, int dy, int dz) {
        return new LocationRange(
            base.getWorld(),
            base.getX(), base.getY(), base.getZ(),
            base.getX() + dx, base.getY() + dy, base.getZ() + dz
        );
    }

    public String getWorld() {
        return world;
    }
}
