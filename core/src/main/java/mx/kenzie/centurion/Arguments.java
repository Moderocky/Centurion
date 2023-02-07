package mx.kenzie.centurion;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Arguments implements Iterable<Object> {
    public static final Argument<Integer> INTEGER = new ArgInteger();
    public static final Argument<Integer> OPTIONAL_INTEGER = new ArgInteger() {
        @Override
        public boolean optional() {
            return true;
        }
    };
    public static final Argument<Long> LONG = new ArgLong();
    public static final Argument<Long> OPTIONAL_LONG = new ArgLong() {
        @Override
        public boolean optional() {
            return true;
        }
    };
    public static final Argument<Double> DOUBLE = new ArgDouble();
    public static final Argument<Double> OPTIONAL_DOUBLE = new ArgDouble() {
        @Override
        public boolean optional() {
            return true;
        }
    };
    public static final Argument<Boolean> BOOLEAN = new ArgBoolean();
    public static final Argument<Boolean> OPTIONAL_BOOLEAN = new ArgBoolean() {
        @Override
        public boolean optional() {
            return true;
        }
    };
    public static final Argument<String> STRING = new ArgString();
    public static final Argument<String> OPTIONAL_STRING = new ArgString() {
        @Override
        public boolean optional() {
            return true;
        }
    };
    public static final Argument<String> GREEDY_STRING = new ArgString() {

        @Override
        public int weight() {
            return super.weight() + 20;
        }

        @Override
        public boolean plural() {
            return true;
        }

    };

    private final List<Object> values;

    Arguments(Object... values) {
        this.values = Arrays.asList(values);
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

abstract class HashedArg<Type> extends TypedArgument<Type> {
    protected int lastHash;
    protected Type lastValue;

    public HashedArg(Class<Type> type) {
        super(type);
    }

    @Override
    public Type parse(String input) {
        if (lastHash == input.hashCode()) return lastValue;
        return this.parseNew(input);
    }

    public abstract Type parseNew(String input);

}

class ArgInteger extends HashedArg<Integer> {

    public ArgInteger() {
        super(Integer.class);
    }

    @Override
    public String label() {
        return "int";
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        for (char c : input.toCharArray()) if (c < '0' || c > '9') return false;
        try {
            this.lastValue = Integer.parseInt(input);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    @Override
    public Integer parseNew(String input) {
        return Integer.parseInt(input.trim());
    }
}

class ArgLong extends HashedArg<Long> {

    public ArgLong() {
        super(Long.class);
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        for (char c : input.toCharArray()) if (c < '0' || c > '9') return false;
        try {
            this.lastValue = Long.parseLong(input);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    @Override
    public Long parseNew(String input) {
        return Long.parseLong(input.trim());
    }
}

class ArgDouble extends HashedArg<Double> {

    public ArgDouble() {
        super(Double.class);
    }

    @Override
    public String label() {
        return "number";
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        if (input.endsWith("D") || input.endsWith("d")) input = input.substring(0, input.length() - 1);
        try {
            this.lastValue = Double.parseDouble(input);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    @Override
    public Double parseNew(String input) {
        if (input.endsWith("D") || input.endsWith("d"))
            return Double.parseDouble(input.substring(0, input.length() - 1));
        return Double.parseDouble(input.trim());
    }
}

class ArgString extends TypedArgument<String> {

    public ArgString() {
        super(String.class);
    }

    @Override
    public boolean matches(String input) {
        return input.length() > 0;
    }

    @Override
    public String parse(String input) {
        return input;
    }

    @Override
    public int weight() {
        return 20;
    }
}

class ArgBoolean extends TypedArgument<Boolean> {
    public ArgBoolean() {
        super(Boolean.class);
    }

    @Override
    public boolean matches(String input) {
        final String parsed = input.toLowerCase().trim();
        return parsed.equals("true") || parsed.equals("false");
    }

    @Override
    public Boolean parse(String input) {
        return input.trim().equalsIgnoreCase("true");
    }
}
