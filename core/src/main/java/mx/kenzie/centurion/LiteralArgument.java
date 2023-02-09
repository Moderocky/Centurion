package mx.kenzie.centurion;

public record LiteralArgument(String label) implements Argument<String> {

    @Override
    public boolean matches(String input) {
        return input.equals(label);
    }

    @Override
    public String parse(String input) {
        return label;
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
