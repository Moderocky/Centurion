package mx.kenzie.centurion;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CompoundArgument<Type> implements Argument<Type> {

    protected final Map<InnerContainer, Converter<Type>> map = new HashMap<>();
    protected final String label;
    protected int lastHash;
    protected Object[] lastInputs;
    protected Converter<Type> lastParser;

    public CompoundArgument(String label) {
        this.label = label;
    }

    public CompoundArgument<Type> arg(Object arg1, Converter<Type> result) {
        return this.arg(List.of(arg1), result);
    }

    public CompoundArgument<Type> arg(Object arg1, Object arg2, Converter<Type> result) {
        return this.arg(List.of(arg1, arg2), result);
    }

    public CompoundArgument<Type> arg(Object arg1, Object arg2, Object arg3, Converter<Type> result) {
        return this.arg(List.of(arg1, arg2, arg3), result);
    }

    public CompoundArgument<Type> arg(Object arg1, Object arg2, Object arg3, Object arg4, Converter<Type> result) {
        return this.arg(List.of(arg1, arg2, arg3, arg4), result);
    }

    public CompoundArgument<Type> arg(Collection<Object> arguments, Converter<Type> result) {
        final List<Argument<?>> list = Command.coerce(arguments);
        final InnerContainer container = new InnerContainer(list.toArray(new Argument[0]));
        this.map.put(container, result);
        return this;
    }

    private void storeResult(InnerContainer.Result result, InnerContainer container) {
        this.lastHash = result.part().hashCode();
        this.lastInputs = result.inputs();
        this.lastParser = map.get(container);
    }

    @Override
    public boolean matches(String input) {
        if (input.hashCode() == lastHash) return true;
        for (InnerContainer container : map.keySet()) {
            final InnerContainer.Result result = container.consume(input, false);
            if (result == null) continue;
            if (result.inputs() == null) continue;
            this.storeResult(result, container);
            return true;
        }
        return false;
    }

    @Override
    public Type parse(String input) {
        if (input.hashCode() == lastHash) return lastParser.apply(new Arguments(lastInputs));
        for (InnerContainer container : map.keySet()) {
            final ArgumentContainer.Result result = container.consume(input, false);
            if (result == null) continue;
            if (result.inputs() == null) continue;
            this.storeResult(result, container);
            return lastParser.apply(new Arguments(lastInputs));
        }
        return null;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public ParseResult read(String input) {
        for (InnerContainer container : map.keySet()) {
            final ArgumentContainer.Result result = container.consume(input, false);
            if (result == null) continue;
            if (result.inputs() == null) continue;
            this.storeResult(result, container);
            return new ParseResult(result.part(), result.remainder());
        }
        return null;
    }

    @FunctionalInterface
    public interface Converter<Result> extends Function<Arguments, Result> {

        @Override
        Result apply(Arguments arguments);

    }

    protected static class InnerContainer extends ArgumentContainer {
        InnerContainer(Argument<?>... arguments) {
            super(arguments);
        }
    }

}
