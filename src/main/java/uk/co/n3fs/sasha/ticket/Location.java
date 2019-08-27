package uk.co.n3fs.sasha.ticket;

import java.util.Objects;

public class Location {

    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public Location(String world, Integer x, Integer y, Integer z) {
        Objects.requireNonNull(world, "No world provided");
        Objects.requireNonNull(x, "No x coordinate provided");
        Objects.requireNonNull(y, "No y coordinate provided");
        Objects.requireNonNull(z, "No z coordinate provided");
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Location relative(int dx, int dy, int dz) {
        return new Location(world, x + dx, y + dy, z + dz);
    }
}
