package mx.kenzie.centurion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Arguments implements Iterable<Object> {
    public static final Argument<Integer> INTEGER = new TypedArgument<>(Integer.class) {
        private int lastHash, lastValue;

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
        public Integer parse(String input) {
            if (lastHash == input.hashCode()) return lastValue;
            return Integer.parseInt(input.trim());
        }
    };
    public static final Argument<Long> LONG = new TypedArgument<>(Long.class) {
        private int lastHash;
        private long lastValue;

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
        public Long parse(String input) {
            if (lastHash == input.hashCode()) return lastValue;
            return Long.parseLong(input.trim());
        }
    };
    public static final Argument<Double> DOUBLE = new TypedArgument<>(Double.class) {
        private int lastHash;
        private double lastValue;

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
        public Double parse(String input) {
            if (lastHash == input.hashCode()) return lastValue;
            if (input.endsWith("D") || input.endsWith("d"))
                return Double.parseDouble(input.substring(0, input.length() - 1));
            return Double.parseDouble(input.trim());
        }
    };
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
    };
    public static final Argument<String> STRING_END = new TypedArgument<>(String.class) {
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
            return super.weight() + 20;
        }

        @Override
        public boolean plural() {
            return true;
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
