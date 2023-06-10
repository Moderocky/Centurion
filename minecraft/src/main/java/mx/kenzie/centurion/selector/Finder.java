package mx.kenzie.centurion.selector;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Finders are the initial `@...` query.
 */
public interface Finder<Type> {

    Finder<CommandSender> SENDER = new SenderFinder();
    Finder<Player> PLAYER = new PlayerFinder(), ALL_PLAYERS = new PlayersFinder(), RANDOM_PLAYER = new RandomFinder();
    Finder<Entity> ALL_ENTITIES = new EntityFinder();

    static <Type> Finder<Type> fixed(String key, List<Type> list) {
        return new FixedFinder<>(key, list);
    }

    static <Type> Finder<Type> fixed(String key, Type... list) {
        return new FixedFinder<>(key, List.of(list));
    }

    /**
     * @return the simple name after the @
     */
    String key();

    Collection<Type> find(CommandSender sender);

}

class SenderFinder implements Finder<CommandSender> {

    @Override
    public String key() {
        return "s";
    }

    @Override
    public Collection<CommandSender> find(CommandSender sender) {
        return Collections.singletonList(sender);
    }

}

class PlayerFinder implements Finder<Player> {

    @Override
    public String key() {
        return "p";
    }

    @Override
    public Collection<Player> find(CommandSender sender) {
        if (sender instanceof Player player) return Collections.singletonList(player);
        final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.size() > 1) return players.subList(0, 1);
        return players;
    }

}

class RandomFinder implements Finder<Player> {

    @Override
    public String key() {
        return "r";
    }

    @Override
    public Collection<Player> find(CommandSender sender) {
        final Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        return Collections.singletonList(players[ThreadLocalRandom.current().nextInt(players.length)]);
    }

}

class PlayersFinder implements Finder<Player> {

    @Override
    public String key() {
        return "a";
    }

    @Override
    public Collection<Player> find(CommandSender sender) {
        return new HashSet<>(Bukkit.getOnlinePlayers());
    }

}

class EntityFinder implements Finder<Entity> {

    @Override
    public String key() {
        return "e";
    }

    @Override
    public Collection<Entity> find(CommandSender sender) {
        final List<Entity> list = new ArrayList<>();
        for (final World world : Bukkit.getWorlds()) list.addAll(world.getEntities());
        return list;
    }

}

record FixedFinder<Type>(String key, List<Type> list) implements Finder<Type> {

    @Override
    public Collection<Type> find(CommandSender sender) {
        return list;
    }

}
