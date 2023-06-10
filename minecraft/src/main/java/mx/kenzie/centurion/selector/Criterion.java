package mx.kenzie.centurion.selector;

import mx.kenzie.centurion.Argument;
import mx.kenzie.centurion.Arguments;
import mx.kenzie.centurion.Command;
import mx.kenzie.centurion.MinecraftCommand;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface Criterion<Type, Test> {

    Criterion<Entity, Predicate<Double>> ENTITY_DISTANCE = of("distance", MinecraftCommand.INEQUALITY,
        (entity, predicate) -> {
            final Command<Object>.Context context = MinecraftCommand.getContext();
            if (context == null) return false;
            if (!(context.getSender() instanceof Entity sender)) return false;
            final Location start = sender.getLocation(), end = entity.getLocation();
            if (!start.getWorld().equals(end.getWorld())) return false;
            return predicate.test(start.distance(end));
        }),
        LEVEL = of("level", MinecraftCommand.INEQUALITY, (entity, predicate) -> {
            if (!(entity instanceof Player player)) return false;
            return predicate.test((double) player.getLevel());
        }),
        X_ROTATION = of("x_rotation", MinecraftCommand.INEQUALITY,
            (entity, predicate) -> predicate.test((double) entity.getLocation().getPitch())),
        Y_ROTATION = of("y_rotation", MinecraftCommand.INEQUALITY,
            (entity, predicate) -> predicate.test((double) entity.getLocation().getYaw()));
    Criterion<Entity, EntityType> ENTITY_TYPE = of("type", MinecraftCommand.ENTITY_TYPE,
        (entity, type) -> entity.getType() != type);
    Criterion<Entity, GameMode> GAME_MODE = of("gamemode", MinecraftCommand.GAME_MODE,
        (entity, mode) -> entity instanceof Player player && player.getGameMode() == mode);
    Criterion<?, Integer> LIMIT = new LimitCriterion();

    static <Type, Test> Criterion<Type, Test> of(String label, Argument<Test> argument, BiPredicate<Type, Test> predicate) {
        return new ArgumentCriterion<>(label, argument, predicate);
    }

    static <Type, Test> Criterion<Type, Test> of(Argument<Test> argument, BiPredicate<Type, Test> predicate) {
        return new ArgumentCriterion<>(argument.label(), argument, predicate);
    }

    String label();

    Argument<Test> argument();

    BiPredicate<Type, Test> predicate();

    default boolean matches(String input) {
        return this.argument().matches(input);
    }

    default Filter<Type> filter(String input) {
        return this.filter(input, false);
    }

    default Filter<Type> filter(String input, boolean invert) {
        final Argument<Test> argument = this.argument();
        if (!argument.matches(input)) throw new IllegalArgumentException(input);
        final Test test = this.argument().parse(input);
        final BiPredicate<Type, Test> predicate;
        if (invert) predicate = this.predicate().negate();
        else predicate = this.predicate();
        return type -> predicate.test(type, test);
    }

}

record ArgumentCriterion<Type, Test>(String label, Argument<Test> argument,
                                     BiPredicate<Type, Test> predicate) implements Criterion<Type, Test> {
}

class LimitCriterion implements Criterion<Object, Integer> {

    @Override
    public String label() {
        return "limit";
    }

    @Override
    public Argument<Integer> argument() {
        return Arguments.INTEGER;
    }

    @Override
    public BiPredicate<Object, Integer> predicate() {
        return new BiPredicate<>() {
            private int counter;

            @Override
            public boolean test(Object o, Integer integer) {
                return counter++ < integer;
            }
        };
    }

}
