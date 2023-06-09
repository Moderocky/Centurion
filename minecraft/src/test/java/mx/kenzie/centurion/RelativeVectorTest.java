package mx.kenzie.centurion;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.junit.Test;

public class RelativeVectorTest {

    @Test
    public void relativeTo() {
        final RelativeVector vector = new RelativeVector(new RelativeNumber(1, true), new RelativeNumber(0, false),
            new RelativeNumber(0, true));
        final Vector two = vector.relativeTo(2, 2, 2);
        assert two != null;
        assert two.getX() == 3;
        assert two.getY() == 0;
        assert two.getZ() == 2;
        final Vector zero = vector.relativeTo(new Vector(0, 0, 0));
        assert zero != null;
        assert zero.getX() == 1;
        assert zero.getY() == 0;
        assert zero.getZ() == 0;
        final Vector minus = vector.relativeTo(new Vector(-5, -7, -1));
        assert minus != null;
        assert minus.getX() == -4;
        assert minus.getY() == 0;
        assert minus.getZ() == -1;
        final Location location = vector.relativeTo(new Location(null, 10, 10, 10));
        assert location != null;
        assert location.getWorld() == null;
        assert location.getX() == 11;
        assert location.getY() == 0;
        assert location.getZ() == 10;
    }

    @Test
    public void isRelative() {
        final RelativeVector vector = new RelativeVector(new RelativeNumber(1, true), new RelativeNumber(0, false),
            new RelativeNumber(0, true));
        assert vector.isRelativeX();
        assert vector.isRelativeZ();
        assert !vector.isRelativeY();
    }

}
