package mx.kenzie.centurion;

import mx.kenzie.centurion.selector.Finder;
import mx.kenzie.centurion.selector.Selector;
import mx.kenzie.centurion.selector.Universe;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SelectorArgument<Type> extends HashedArg<Selector<Type>> {

    protected final Universe<Type> universe;

    @SuppressWarnings("unchecked")
    public SelectorArgument(Universe<Type> universe) {
        super((Class<Selector<Type>>) (Object) Selector.class);
        this.universe = universe;
    }

    @Override
    public boolean matches(String input) {
        return Selector.validate(input, universe);
    }

    @Override
    public Selector<Type> parseNew(String input) {
        return Selector.of(input, universe);
    }

    @Override
    public String[] possibilities() {
        final Set<String> list = new HashSet<>();
        final Command<CommandSender>.Context context = MinecraftCommand.getContext();
        if (context == null || context.rawInput.isBlank()) {
            for (final Finder<? extends Type> finder : universe.finders()) {
                list.add("@" + finder.key());
            }
            return list.toArray(new String[0]);
        }
        final Selector.PositionResult result = Selector.position(context.rawInput, universe);
        return result.suggestions();
    }

}

class MinecraftSelectorArgument extends SelectorArgument<Entity> {

    public MinecraftSelectorArgument() {
        super(Universe.of());
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        this.lastValue = null;
        try {
            final MinecraftSelector selector = this.parseNew(input);
            this.lastValue = selector;
            return selector.verify();
        } catch (Throwable ex) {
            return false;
        }
    }

    @Override
    public MinecraftSelector parseNew(String input) {
        return new MinecraftSelector(input);
    }

    @Override
    public String[] possibilities() {
        if (possibilities.length > 0) return possibilities;
        final List<String> list = new ArrayList<>();
        list.add("@s");
        list.add("@p");
        list.add("@a");
        list.add("@e");
        list.add("@r");
        if (Bukkit.getServer() != null) for (Player player : Bukkit.getOnlinePlayers()) list.add(player.getName());
        return list.toArray(new String[0]);
    }

}
