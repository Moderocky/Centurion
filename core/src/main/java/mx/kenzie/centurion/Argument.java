package mx.kenzie.centurion;

public interface Argument<Type> extends ArgumentFace {

    boolean matches(String input);

    Type parse(String input);

    default int weight() {
        return this.plural() ? 100 : 10;
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

}
