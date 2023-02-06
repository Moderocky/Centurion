package mx.kenzie.centurion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Arguments implements Iterable<Object> {
    public static final Argument<Boolean> BOOLEAN = new TypedArgument<>(Boolean.class) {
        @Override
        public boolean matches(String input) {
            final String parsed = input.toLowerCase().trim();
            return parsed.equals("true") || parsed.equals("false");
        }

        @Override
        public Boolean parse(String input) {
            return Boolean.parseBoolean(input.toLowerCase().trim());
        }
    };
    public static final Argument<String> STRING = new TypedArgument<>(String.class) {
        @Override
        public boolean matches(String input) {
            return true;
        }

        @Override
        public String parse(String input) {
            return input;
        }

        @Override
        public int weight() {
            return 20;
        }
    };

    private final ArrayList<Object> values;

    Arguments(Object... values) {
        this.values = new ArrayList<>(List.of(values));
    }

    @SuppressWarnings("unchecked")
    public <Type> Type get(int index) {
        if (index >= values.size()) return null;
        return (Type) values.get(index);
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return values.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arguments arguments)) return false;
        return Objects.equals(values, arguments.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public String toString() {
        return "Arguments" + values;
    }

}
