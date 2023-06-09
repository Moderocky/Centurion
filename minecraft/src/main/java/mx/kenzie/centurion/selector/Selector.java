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

    public static <Type> Selector<Type> of(String selector, Universe<Type> universe) {
        return new SelectorParser<>(selector, universe).parse();
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

}
