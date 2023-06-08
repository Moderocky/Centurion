package mx.kenzie.centurion.selector;

import java.util.HashSet;
import java.util.Set;

public record Universe<Type>(Set<Finder<? extends Type>> finders, Set<Criterion<? extends Type, ?>> criteria) {

    @SuppressWarnings("unchecked")
    public static <Type> Universe<Type> of(Object... things) {
        final Set<Finder<? extends Type>> finders = new HashSet<>();
        final Set<Criterion<? extends Type, ?>> criteria = new HashSet<>();
        for (final Object thing : things) {
            if (thing instanceof Finder<?> finder) finders.add((Finder<? extends Type>) finder);
            else if (thing instanceof Criterion<?, ?> criterion) criteria.add((Criterion<? extends Type, ?>) criterion);
        }
        return new Universe<>(finders, criteria);
    }

}
