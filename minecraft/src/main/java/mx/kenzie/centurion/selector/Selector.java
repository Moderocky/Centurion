package mx.kenzie.centurion.selector;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Selector<Type> {

    protected final Finder<Type> finder;
    protected final Filter<Type>[] filters;

    @SafeVarargs
    public Selector(Finder<Type> finder, Filter<Type>... filters) {
        this.finder = finder;
        this.filters = filters;
    }

    public static <Type> PositionResult position(String input, Universe<Type> universe) {
        if (input == null) return new PositionResult(Caret.OTHER);
        if (input.endsWith(" ")) return new SelectorParser<>("", universe).suggest();
        final String[] args = input.split(" ");
        final String source = args[args.length - 1];
        final SelectorParser<Type> parser = new SelectorParser<>(source, universe);
        return parser.suggest();

    }

    public static <Type> boolean validate(String selector, Universe<Type> universe) {
        return new SelectorParser<>(selector, universe).validate();
    }

    @SuppressWarnings("unchecked")
    public static <Result, Type extends Result> Selector<Result> of(String selector, Universe<Type> universe) {
        return (Selector<Result>) new SelectorParser<>(selector, universe).parse();
    }

    @SafeVarargs
    public static <Type> Selector<Type> fixed(Type... inputs) {
        return new Selector<>(null) {
            final Type[] values = inputs;

            @Override
            public List<Type> getAll(CommandSender sender) {
                return List.of(values);
            }

            @Override
            public Type getOne(CommandSender sender) {
                return values.length > 0 ? values[0] : null;
            }
        };
    }

    public static <Type> Selector<Type> empty() {
        return new Selector<>(null) {
            @Override
            public List<Type> getAll(CommandSender sender) {
                return new ArrayList<>();
            }

            @Override
            public Type getOne(CommandSender sender) {
                return null;
            }
        };
    }

    public List<Type> getAll(CommandSender sender) {
        final List<Type> list = new LinkedList<>(finder.find(sender));
        for (final Filter<Type> filter : filters) list.removeIf(filter);
        return list;
    }

    public Type getOne(CommandSender sender) {
        final List<Type> list = new ArrayList<>(finder.find(sender));
        check:
        for (final Type type : list) {
            for (final Filter<Type> filter : filters) if (filter.test(type)) continue check;
            return type;
        }
        return null;
    }

    public record PositionResult(Caret caret, String... suggestions) {}

}
