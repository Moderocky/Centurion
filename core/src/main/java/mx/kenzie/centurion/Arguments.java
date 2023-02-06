package mx.kenzie.centurion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    };


    private final ArrayList<Object> values;

    public Arguments(Object... values) {
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

}
