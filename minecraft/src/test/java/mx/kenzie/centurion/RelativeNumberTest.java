package mx.kenzie.centurion;

import org.junit.Test;

public class RelativeNumberTest {

    @Test
    public void intValue() {
        assert new RelativeNumber(1, false).intValue() == 1;
        assert new RelativeNumber(1, true).intValue() == 1;
        assert new RelativeNumber(-44, true).intValue() == -44;
        assert new RelativeNumber(-44, false).intValue() == -44;
        assert new RelativeNumber(610023, true).intValue() == 610023;
        assert new RelativeNumber(610023, false).intValue() == 610023;
        assert new RelativeNumber(610023.5, true).intValue() == 610023;
        assert new RelativeNumber(610023.9, false).intValue() == 610023;
        assert new RelativeNumber(-2.0448111, true).intValue() == -2;
        assert new RelativeNumber(-4.581922001, false).intValue() == -4;
        assert new RelativeNumber(-4.001, false).intValue() == -4;
        assert new RelativeNumber(-4.999, false).intValue() == -4;
        assert new RelativeNumber(4.001, false).intValue() == 4;
        assert new RelativeNumber(4.999, false).intValue() == 4;
    }

    @Test
    public void longValue() {
        assert new RelativeNumber(1, false).longValue() == 1;
        assert new RelativeNumber(1, true).longValue() == 1;
        assert new RelativeNumber(-44, true).longValue() == -44;
        assert new RelativeNumber(-44, false).longValue() == -44;
        assert new RelativeNumber(610023, true).longValue() == 610023;
        assert new RelativeNumber(610023, false).longValue() == 610023;
        assert new RelativeNumber(610023.5, true).longValue() == 610023;
        assert new RelativeNumber(610023.9, false).longValue() == 610023;
        assert new RelativeNumber(-2.0448111, true).longValue() == -2;
        assert new RelativeNumber(-4.581922001, false).longValue() == -4;
        assert new RelativeNumber(-4.001, false).longValue() == -4;
        assert new RelativeNumber(-4.999, false).longValue() == -4;
        assert new RelativeNumber(4.001, false).longValue() == 4;
        assert new RelativeNumber(4.999, false).longValue() == 4;
        assert new RelativeNumber(10000000000000L, false).longValue() == 10000000000000L;
        assert new RelativeNumber(-10000000000000L, true).longValue() == -10000000000000L;
    }

    @Test
    public void floatValue() {
        assert new RelativeNumber(1, false).floatValue() == 1;
        assert new RelativeNumber(1, true).floatValue() == 1;
        assert new RelativeNumber(1.5F, false).floatValue() == 1.5F;
        assert new RelativeNumber(1.3F, true).floatValue() == 1.3F;
        assert new RelativeNumber(-1.5F, false).floatValue() == -1.5F;
        assert new RelativeNumber(-1.3F, true).floatValue() == -1.3F;
    }

    @Test
    public void doubleValue() {
        assert new RelativeNumber(1, false).doubleValue() == 1;
        assert new RelativeNumber(1, true).doubleValue() == 1;
        assert new RelativeNumber(1.5, false).doubleValue() == 1.5;
        assert new RelativeNumber(1.3, true).doubleValue() == 1.3;
        assert new RelativeNumber(-1.5, false).doubleValue() == -1.5;
        assert new RelativeNumber(-1.3, true).doubleValue() == -1.3;
    }

    @Test
    public void isRelative() {
    }

    @Test
    public void getNumber() {
    }
}
