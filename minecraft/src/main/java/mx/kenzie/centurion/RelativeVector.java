package mx.kenzie.centurion;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Objects;

public class RelativeVector extends Vector {

    private boolean relativeX, relativeY, relativeZ;

    public RelativeVector() {
        super();
    }

    public RelativeVector(RelativeNumber x, RelativeNumber y, RelativeNumber z) {
        super(x.doubleValue(), y.doubleValue(), z.doubleValue());
        this.relativeX = x.isRelative();
        this.relativeY = y.isRelative();
        this.relativeZ = z.isRelative();
    }

    public static RelativeVector ofAll(double x, double y, double z) {
        return new RelativeVector(RelativeNumber.of(x), RelativeNumber.of(y), RelativeNumber.of(z));
    }

    public static RelativeVector of(Vector vector) {
        return ofAll(vector.getX(), vector.getY(), vector.getZ());
    }

    public Location relativeTo(Entity entity) {
        return this.relativeTo(entity.getLocation());
    }

    public Location relativeTo(Block block) {
        return this.relativeTo(block.getLocation());
    }

    public Location relativeTo(Location location) {
        return this.relativeTo(location.toVector()).toLocation(location.getWorld());
    }

    public Vector relativeTo(Vector vector) {
        return new Vector(
            relativeX ? vector.getX() + x : x,
            relativeY ? vector.getY() + y : y,
            relativeZ ? vector.getZ() + z : z
        );
    }

    public Vector relativeTo(double x, double y, double z) {
        return new Vector(
            relativeX ? x + this.x : this.x,
            relativeY ? y + this.y : this.y,
            relativeZ ? z + this.z : this.z
        );
    }

    public boolean isRelativeX() {
        return relativeX;
    }

    public boolean isRelativeY() {
        return relativeY;
    }

    public boolean isRelativeZ() {
        return relativeZ;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof RelativeVector vector) {
            if (relativeX != vector.relativeX) return false;
            if (relativeY != vector.relativeY) return false;
            if (relativeZ != vector.relativeZ) return false;
        }
        if (!(obj instanceof Vector vector)) return false;
        return Math.abs(this.x - vector.getX()) < 1.0E-6 && Math.abs(this.y - vector.getY()) < 1.0E-6 && Math.abs(this.z - vector.getZ()) < 1.0E-6;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relativeX, relativeY, relativeZ);
    }

}
