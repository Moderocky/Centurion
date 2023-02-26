package mx.kenzie.centurion;

import org.bukkit.util.Vector;
import org.junit.Test;

public class LocalVectorTest {

    @Test
    public void relativeToPosition() {
        final Vector vector = new Vector(10, 10, 10);
        this.test(new LocalVector(0, 2, 0), vector, new Vector(0, 0, 1), new Vector(10, 12, 10));
        this.test(new LocalVector(2, 0, 0), vector, new Vector(0, 0, 1), new Vector(12, 10, 10));
        this.test(new LocalVector(0, 0, 2), vector, new Vector(0, 0, 1), new Vector(10, 10, 12));
        this.test(new LocalVector(0, 2, 0), vector, new Vector(0, 1, 0), new Vector(10, 10, 8));
        this.test(new LocalVector(0, 2, 0), vector, new Vector(1, 0, 0), new Vector(10, 12, 10));
    }

    protected void test(LocalVector input, Vector initial, Vector rotation, Vector end) {
        final Vector result = input.relativeTo(initial, rotation);
        assert result.equals(end) : result;
    }

}
