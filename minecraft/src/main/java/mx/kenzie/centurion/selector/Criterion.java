package mx.kenzie.centurion.selector;

import mx.kenzie.centurion.Argument;

import java.util.function.BiPredicate;

public interface Criterion<Type, Test> {

    static <Type, Test> Criterion<Type, Test> of(String label, Argument<Test> argument, BiPredicate<Type, Test> predicate) {
        return new ArgumentCriterion<>(label, argument, predicate);
    }

    String label();

    Argument<Test> argument();

    BiPredicate<Type, Test> predicate();

    default boolean matches(String input) {
        return this.argument().matches(input);
    }

    default Filter<Type> filter(String input) {
        final Argument<Test> argument = this.argument();
        if (!argument.matches(input)) throw new IllegalArgumentException(input);
        final Test test = this.argument().parse(input);
        final BiPredicate<Type, Test> predicate = this.predicate();
        return type -> predicate.test(type, test);
    }

}

record ArgumentCriterion<Type, Test>(String label, Argument<Test> argument,
                                     BiPredicate<Type, Test> predicate) implements Criterion<Type, Test> {
}
