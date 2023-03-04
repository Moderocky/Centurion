package mx.kenzie.centurion;

import java.util.*;
import java.util.function.Function;

public class CompoundArgument<Type> extends TypedArgument<Type> implements Argument<Type>, Cloneable {

    protected final Map<InnerContainer, Converter<Type>> map;
    protected final List<InnerContainer> arguments;
    protected boolean sorted;
    protected int lastHash;
    protected Object[] lastInputs;
    protected Converter<Type> lastParser;

    protected CompoundArgument(String label, Class<Type> type, Map<InnerContainer, Converter<Type>> map, List<InnerContainer> arguments) {
        super(type);
        this.label = label;
        this.map = map;
        this.arguments = arguments;
    }

    public CompoundArgument(String label, Class<Type> type) {
        super(type);
        this.label = label;
        this.map = new HashMap<>();
        this.arguments = new ArrayList<>();
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
        this.sorted = false;
        final List<Argument<?>> list = Command.coerce(arguments);
        final InnerContainer container = new InnerContainer(list.toArray(new Argument[0]));
        this.map.put(container, result);
        this.arguments.add(container);
        return this;
    }

    protected void sort() {
        if (sorted) return;
        this.sorted = true;
        this.arguments.sort(Comparator.comparing(ArgumentContainer::weight));
    }

    private void storeResult(InnerContainer.Result result, InnerContainer container) {
        this.lastHash = result.part().hashCode();
        this.lastInputs = result.inputs();
        this.lastParser = map.get(container);
    }

    @Override
    public boolean matches(String input) {
        if (input.hashCode() == lastHash) return true;
        this.sort();
        for (InnerContainer container : arguments) {
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
        this.sort();
        for (InnerContainer container : arguments) {
            final ArgumentContainer.Result result = container.consume(input, false);
            if (result == null) continue;
            if (result.inputs() == null) continue;
            this.storeResult(result, container);
            return lastParser.apply(new Arguments(lastInputs));
        }
        return null;
    }

    @Override
    public ParseResult read(String input) {
        this.sort();
        for (InnerContainer container : arguments) {
            final ArgumentContainer.Result result = container.consume(input, false);
            if (result == null) continue;
            if (result.inputs() == null) continue;
            this.storeResult(result, container);
            return new ParseResult(result.part(), result.remainder());
        }
        return null;
    }

    @Override
    public int weight() {
        return super.weight() - 2;
    }

    @Override
    public String[] possibilities() {
        final List<String> list = new ArrayList<>(32);
        for (InnerContainer argument : arguments) list.addAll(argument.possibilities());
        return list.toArray(new String[0]);
    }

    @Override
    public final CompoundArgument<Type> clone() {
        return new CompoundArgument<>(label, type, map, arguments);
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

        public List<String> possibilities() {
            if (arguments.length == 0) return Collections.emptyList();
            if (arguments.length == 1) return List.of(arguments[0].possibilities());
            final String[] strings = arguments[0].possibilities();
            if (strings.length < 1) return Collections.emptyList();
            final List<String> possibilities = new ArrayList<>(24);
            for (String possibility : arguments[0].possibilities()) this.scrape(possibility, 1, possibilities);
            return possibilities;
        }

        private void scrape(String bit, int index, List<String> possibilities) {
            final Argument<?> argument = arguments[index++];
            final String[] strings = argument.possibilities();
            if (strings.length < 1) {
                possibilities.add(bit);
                return;
            }
            for (String possibility : strings) {
                final String stub = bit + ' ' + possibility;
                if (arguments.length > index) this.scrape(stub, index, possibilities);
                else possibilities.add(stub);
            }
        }

    }

}
