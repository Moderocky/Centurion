package mx.kenzie.centurion;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public class Arguments implements Iterable<Object> {
    public static final TypedArgument<ArgumentContainer> PATTERN = new PatternArgument();
    public static final TypedArgument<Class> CLASS = new ArgClass();
    public static final TypedArgument<Integer> INTEGER = new ArgInteger();
    public static final TypedArgument<Long> LONG = new ArgLong();
    public static final TypedArgument<Double> DOUBLE = new ArgDouble();
    public static final TypedArgument<Boolean> BOOLEAN = new ArgBoolean();
    public static final TypedArgument<String> STRING = new ArgString();
    public static final TypedArgument<String> GREEDY_STRING = new ArgString() {

        @Override
        public int weight() {
            return super.weight() + 20;
        }

        @Override
        public boolean plural() {
            return true;
        }

    };
    public static final TypedArgument<Argument> ARGUMENT = new ArgumentArgument();

    private final List<Object> values;
    private final Map<Argument<?>, Object> map;

    Arguments(Object... values) {
        this.values = Arrays.asList(values);
        this.map = new HashMap<>();
    }

    Arguments(ArgumentContainer container, Object... values) {
        this.values = Arrays.asList(values);
        this.map = new HashMap<>();
        final Command<?>.Context context = Command.getContext();
        if (context == null || context.getCommand() == null) this.unwrapArguments(container, false);
        else this.unwrapArguments(container, context.getCommand().behaviour.passAllArguments);
    }

    private void unwrapArguments(ArgumentContainer container, boolean passAllArguments) {
        final Iterator<Object> iterator = values.iterator();
        for (Argument<?> argument : container.arguments) {
            if (!passAllArguments && argument.literal()) continue;
            if (!iterator.hasNext()) break;
            this.map.put(argument, iterator.next());
        }
    }

    @SuppressWarnings("unchecked")
    public <Type> Type get(int index) {
        if (index >= values.size()) return null;
        return (Type) values.get(index);
    }

    @SuppressWarnings("unchecked")
    public <Type> Type get(Class<Type> type) {
        for (Object value : values) if (type.isInstance(value)) return (Type) value;
        return null;
    }

    @SuppressWarnings("unchecked")
    public <Type> Type get(Class<Type> type, int index) {
        for (Object value : values) {
            if (!type.isInstance(value)) continue;
            if (--index < 0) return (Type) value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <Type> Type get(Argument<Type> type) {
        return (Type) map.get(type);
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
        this.label = "int";
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        for (char c : input.toCharArray()) if ((c < '0' || c > '9') && c != '-') return false;
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
        for (char c : input.toCharArray()) if ((c < '0' || c > '9') && c != '-') return false;
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
        this.label = "number";
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
        return super.weight() + 10;
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

    @Override
    public String[] possibilities() {
        return new String[]{"true", "false"};
    }

    @Override
    public Boolean lapse() {
        return false;
    }

}

class ArgClass extends HashedArg<Class> {
    private static final Pattern PART = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
    private static final Pattern CLASS = Pattern.compile(PART + "(\\." + PART + ")*");

    public ArgClass() {
        super(Class.class);
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        if (!CLASS.matcher(input).matches()) return false;
        return (lastValue = this.parseNew(input)) != null;
    }

    @Override
    public Class<?> parseNew(String input) {
        try {
            return Class.forName(input.trim());
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }


}
