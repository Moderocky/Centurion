package mx.kenzie.centurion;

public interface Argument<Type> extends ArgumentFace {

    boolean matches(String input);

    Type parse(String input);

    default int weight() {
        return this.plural() ? 100 : 10;
    }

}

@interface ArgumentFace {
    String label();

    boolean plural() default false;

    int weight() default 10;

    boolean literal() default false;
}
