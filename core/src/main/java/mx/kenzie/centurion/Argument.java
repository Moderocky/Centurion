package mx.kenzie.centurion;

public interface Argument<Type> extends ArgumentFace {

    boolean matches(String input);

    Type parse(String input);

    default int weight() {
        return this.plural() ? 100 : this.literal() ? 3 : 10;
    }

    default Type lapse() {
        return null;
    }

    default ParseResult read(String input) {
        final int space = input.indexOf(' ');
        if (this.plural() || space < 0) return new ParseResult(input.trim(), "");
        else return new ParseResult(input.substring(0, space).trim(), input.substring(space + 1).stripLeading());
    }

    record ParseResult(String part, String remainder) {
    }

}

interface ArgumentFace {
    String label();

    default boolean plural() {
        return false;
    }

    default boolean optional() {
        return false;
    }

    default int weight() {
        return 10;
    }

    default boolean literal() {
        return false;
    }

    default String[] possibilities() {
        return new String[0];
    }

}
