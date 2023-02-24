package mx.kenzie.centurion;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

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

}
