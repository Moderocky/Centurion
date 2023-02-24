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
    }

    @Test
    public void floatValue() {
    }

    @Test
    public void doubleValue() {
    }

    @Test
    public void isRelative() {
    }

    @Test
    public void getNumber() {
    }
}
