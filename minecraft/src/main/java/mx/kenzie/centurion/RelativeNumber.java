package mx.kenzie.centurion;

public class RelativeNumber extends Number {
    private final Number number;
    private final boolean relative;

    public RelativeNumber(Number number, boolean relative) {
        this.number = number;
        this.relative = relative;
    }

    public static RelativeNumber of(Number number) {
        return new RelativeNumber(number, true);
    }

    @Override
    public int intValue() {
        return number.intValue();
    }

    @Override
    public long longValue() {
        return number.longValue();
    }

    @Override
    public float floatValue() {
        return number.floatValue();
    }

    @Override
    public double doubleValue() {
        return number.doubleValue();
    }

    public boolean isRelative() {
        return relative;
    }

    public Number getNumber() {
        return number;
    }

}
