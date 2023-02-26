package mx.kenzie.centurion;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class LocalVector extends RelativeVector {

    private static final double TAU = 2 * Math.PI;

    public LocalVector() {
        this(RelativeNumber.of(0), RelativeNumber.of(0), RelativeNumber.of(0));
    }

    public LocalVector(Number x, Number y, Number z) {
        this(RelativeNumber.of(x), RelativeNumber.of(y), RelativeNumber.of(z));
    }

    protected LocalVector(RelativeNumber x, RelativeNumber y, RelativeNumber z) {
        super(x, y, z);
    }

    @Override
    public Location relativeTo(Entity entity) {
        return super.relativeTo(entity);
    }

    @Override
    public Location relativeTo(Block block) {
        return super.relativeTo(block);
    }

    @Override
    public Location relativeTo(Location location) {
        return this.relativeTo(location.toVector(), location.getDirection()).toLocation(location.getWorld());
    }

    @Override
    public Vector relativeTo(Vector vector) {
        return this.relativeTo(vector, new Vector(0, 0, 1));
    }

    @Override
    public Vector relativeTo(double x, double y, double z) {
        return this.relativeTo(new Vector(x, y, z), new Vector(0, 0, 1));
    }

    public Vector relativeTo(Vector vector, double yaw, double pitch) {
        final double radian = 0.017453292519943295;
        final double right = radian * (yaw + 90), back = -pitch * radian, up = radian * (-pitch + 90);
        final double cosRight = Math.cos(right), sinRight = Math.sin(right);
        final double cosUp = Math.cos(up), cosBack = Math.cos(back);
        final Vector sway = new Vector(cosRight * cosUp, Math.sin(up), sinRight * cosUp);
        final Vector surge = new Vector(cosRight * cosBack, Math.sin(back), sinRight * cosBack);
        final Vector heave = surge.clone().crossProduct(sway).multiply(-1);
        return new Vector(
            (surge.getX() * this.getZ()) + (sway.getX() * this.getY()) + (heave.getX() * this.getX()) + vector.getX(),
            (surge.getY() * this.getZ()) + (sway.getY() * this.getY()) + (heave.getY() * this.getX()) + vector.getY(),
            (surge.getZ() * this.getZ()) + (sway.getZ() * this.getY()) + (heave.getZ() * this.getX()) + vector.getZ()
        );
    }

    public Vector relativeTo(Vector vector, Vector direction) {
        final Vector orientation = direction.isNormalized() ? direction : direction.normalize();
        final double yaw, pitch;
        final double x = orientation.getX(), z = orientation.getZ();
        if (x == 0 && z == 0) {
            pitch = orientation.getY() > 0 ? -90 : 90;
            yaw = 0;
        } else {
            final double theta = Math.atan2(-x, z);
            yaw = Math.toDegrees((theta + TAU) % TAU);
            double xz = Math.sqrt((x * x) + (z * z));
            pitch = Math.toDegrees(Math.atan(-orientation.getY() / xz));
        }
        return this.relativeTo(vector, yaw, pitch);
    }

}
