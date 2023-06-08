package mx.kenzie.centurion.selector;

import java.util.function.Predicate;

public interface Filter<Type> extends Predicate<Type> {

    boolean matches(Type type);

    @Override
    default boolean test(Type type) {
        return !this.matches(type);
    }

}
