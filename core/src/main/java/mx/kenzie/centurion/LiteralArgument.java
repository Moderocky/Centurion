package mx.kenzie.centurion;

public record LiteralArgument(String label) implements Argument<Void> {

    @Override
    public boolean matches(String input) {
        return input.equals(label);
    }

    @Override
    public Void parse(String input) {
        return null;
    }

    @Override
    public boolean literal() {
        return true;
    }

    @Override
    public int weight() {
        return 3;
    }

    @Override
    public String[] possibilities() {
        return new String[]{label};
    }

}
