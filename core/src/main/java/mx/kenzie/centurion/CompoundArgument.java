package mx.kenzie.centurion;

import java.util.*;
import java.util.function.Function;

public abstract class CompoundArgument<Type> implements Argument<Type> {

    protected final Map<ArgumentContainer, Converter<Type>> map = new HashMap<>();
    protected int lastHash;
    protected Object[] lastInputs;

    public CompoundArgument() {
    }

    public CompoundArgument<Type> arg(Object arg1, Converter<Type> result) {
        this.arg(List.of(arg1), result);
        return this;
    }

    public CompoundArgument<Type> arg(Object arg1, Object arg2, Converter<Type> result) {
        this.arg(List.of(arg1, arg2), result);
        return this;
    }

    public CompoundArgument<Type> arg(Object arg1, Object arg2, Object arg3, Converter<Type> result) {
        this.arg(List.of(arg1, arg2, arg3), result);
        return this;
    }

    public CompoundArgument<Type> arg(Object arg1, Object arg2, Object arg3, Object arg4, Converter<Type> result) {
        this.arg(List.of(arg1, arg2, arg3, arg4), result);
        return this;
    }

    public CompoundArgument<Type> arg(Collection<Object> arguments, Converter<Type> result) {
        final List<Argument<?>> list = new ArrayList<>(arguments.size());
        for (Object argument : arguments) {
            if (argument instanceof String string) list.add(new LiteralArgument(string));
            else if (argument instanceof Argument<?> arg) list.add(arg);
            else if (argument == Boolean.class) list.add(Arguments.BOOLEAN);
            else if (argument == String.class) list.add(Arguments.STRING);
            else throw new RuntimeException("Unknown argument acceptor provided. " + argument);
        }
        final ArgumentContainer container = new ArgumentContainer(list.toArray(new Argument[0]));
        this.map.put(container, result);
        return this;
    }

    public abstract Type apply(Object... objects);

    @Override
    public boolean matches(String input) {
        return false;
    }

    @Override
    public Type parse(String input) {
        return null;
    }

    @Override
    public String label() {
        return null;
    }

    @Override
    public ParseResult read(String input) {
        for (ArgumentContainer container : map.keySet()) {
            final ArgumentContainer.Result result = container.check(input, false);
            if (result == null) continue;
            if (result.inputs == null) continue;
            this.lastHash = result.part.hashCode();
            this.lastInputs = result.inputs;
            return new ParseResult(result.part, result.remainder);
        }
        final int space = input.indexOf(' ');
        if (this.plural() || space < 0) return new ParseResult(input.trim(), "");
        else return new ParseResult(input.substring(0, space).trim(), input.substring(space + 1).stripLeading());
    }

    @FunctionalInterface
    public interface Converter<Result> extends Function<Arguments, Result> {

        @Override
        Result apply(Arguments arguments);

    }

    protected record ArgumentContainer(Argument<?>... arguments) {

        public int weight() {
            int weight = 0;
            for (Argument<?> argument : arguments) weight += argument.weight();
            return weight;
        }

        public int size() {
            return arguments.length;
        }

        public boolean hasInput() {
            for (Argument<?> argument : arguments) if (!argument.literal()) return true;
            return false;
        }

        public boolean hasOptional() {
            for (Argument<?> argument : arguments) if (argument.optional()) return true;
            return false;
        }

        public Result check(String initial, boolean passAllArguments) {
            String input = initial;
            final List<Object> inputs = new ArrayList<>(8);
            for (Argument<?> argument : arguments) {
                final Argument.ParseResult result = argument.read(input);
                final String part = result.part(), remainder = result.remainder();
                if (part.isEmpty() && argument.optional()) {
                    inputs.add(argument.lapse());
                    input = remainder;
                    continue;
                }
                if (!argument.matches(part)) return null;
                if (!passAllArguments && argument.literal()) {
                    input = remainder;
                    continue;
                }
                inputs.add(argument.parse(part));
                input = remainder;
            }
            final String part = initial.substring(0, initial.length() - input.length()).trim();
            final String remainder = input.stripLeading();
            return new Result(part, remainder, inputs.toArray(new Object[0]));
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            for (Argument<?> argument : arguments) {
                builder.append(' ');
                final boolean optional = argument.optional(), literal = argument.literal(), plural = argument.plural();
                if (optional) builder.append('[');
                else if (!literal) builder.append('<');
                builder.append(argument.label());
                if (plural) builder.append("...");
                if (optional) builder.append(']');
                else if (!literal) builder.append('>');
            }
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ArgumentContainer that)) return false;
            return Arrays.equals(arguments, that.arguments);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(arguments);
        }

        protected record Result(String part, String remainder, Object... inputs) {

        }

    }

}
