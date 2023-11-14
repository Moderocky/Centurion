package mx.kenzie.centurion;

import java.util.*;
import java.util.function.Function;

public class AnyArgument<Type> extends TypedArgument<Type> {

    @SuppressWarnings("RawUseOfParameterized")
    private static final Function self = o -> o;
    protected final Map<Argument<?>, Function<?, Type>> choices;
    protected final List<Argument<?>> arguments;
    private int lastInput;
    private Argument<?> lastArgument;
    private boolean clean;

    public AnyArgument(Class<Type> type) {
        super(type);
        this.choices = new HashMap<>();
        this.arguments = new LinkedList<>();
        this.label = null;
    }

    @SafeVarargs
    public static <Type> AnyArgument<Type> of(TypedArgument<Type> first, Argument<Type>... others) {
        final AnyArgument<Type> any = new AnyArgument<>(first.type);
        any.with(first);
        for (final Argument<Type> other : others) any.with(other);
        return any;
    }

    @SuppressWarnings({"unchecked", "RawUseOfParameterized"})
    public static AnyArgument<Object> of(Argument<?>... arguments) {
        final AnyArgument<Object> any = new AnyArgument<>(Object.class);
        for (final Argument argument : arguments) any.with(argument);
        return any;
    }

    public <Value> AnyArgument<Type> with(Argument<Value> argument, Function<Value, Type> converter) {
        this.clean = false;
        this.arguments.add(argument);
        this.choices.put(argument, converter);
        if (label == null) label = argument.label();
        else label = label + '/' + argument.label();
        return this;
    }

    @SuppressWarnings("unchecked")
    public AnyArgument<Type> with(Argument<Type> argument) {
        return this.with(argument, self);
    }

    @Override
    public String label() {
        if (label == null) return "unknown";
        return super.label();
    }

    @Override
    public boolean matches(String input) {
        this.lastInput = 0;
        this.lastArgument = null;
        if (choices.isEmpty()) return false;
        this.clean();
        for (final Argument<?> argument : arguments)
            if (argument.matches(input)) {
                this.lastInput = input.hashCode();
                this.lastArgument = argument;
                return true;
            }
        return false;
    }

    @Override
    public Type parse(String input) {
        final Argument<?> argument;
        choose:
        if (lastInput == input.hashCode() && lastArgument != null) argument = lastArgument;
        else {
            this.clean();
            for (final Argument<?> choice : choices.keySet())
                if (choice.matches(input)) {
                    this.lastInput = input.hashCode();
                    this.lastArgument = argument = choice;
                    break choose;
                }
            throw new IllegalArgumentException();
        }
        final Function<?, Type> function = choices.get(argument);
        assert function != null;
        final Object parsed = argument.parse(input);
        return this.extract(parsed, function);
    }

    @Override
    public String[] possibilities() {
        final String[] strings = super.possibilities();
        if (strings != null && strings.length > 0) return strings;
        final Set<String> set = new LinkedHashSet<>();
        for (final Argument<?> argument : arguments) set.addAll(List.of(argument.possibilities()));
        return set.toArray(new String[0]);
    }

    @SuppressWarnings({"unchecked", "RawUseOfParameterized"})
    private Type extract(Object value, Function function) {
        return (Type) function.apply(value);
    }

    private void clean() {
        if (clean) return;
        this.clean = true;
        this.arguments.sort(Comparator.comparing(Argument::weight));
    }

}
