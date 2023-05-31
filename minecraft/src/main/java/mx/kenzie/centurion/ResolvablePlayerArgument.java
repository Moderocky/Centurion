package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

class ResolvablePlayerArgument<Type extends OfflinePlayer> extends HashedArg<Type> {

    protected boolean resolveNames;

    @SuppressWarnings("unchecked")
    public ResolvablePlayerArgument(boolean resolveNames) {
        this((Class<Type>) OfflinePlayer.class);
        this.resolveNames = resolveNames;
    }

    ResolvablePlayerArgument(Class<Type> type) {
        super(type);
    }

    @Override
    public boolean matches(String input) {
        this.lastValue = this.parseNew(input);
        this.lastHash = input.hashCode();
        return lastValue != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Type parseNew(String input) {
        if (Bukkit.getPlayer(input) != null) return (Type) Bukkit.getPlayer(input);
        try {
            return (Type) Bukkit.getOfflinePlayer(UUID.fromString(input));
        } catch (Throwable ignore) {
        }
        if (resolveNames) return (Type) Bukkit.getOfflinePlayer(input);
        else return (Type) Bukkit.getOfflinePlayerIfCached(input);
    }

    @Override
    public String[] possibilities() {
        if (possibilities.length > 0) return possibilities;
        if (Bukkit.getServer() == null) return new String[0];
        final Set<String> set = new LinkedHashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) set.add(player.getName());
        for (final OfflinePlayer player : Bukkit.getOfflinePlayers()) set.add(player.getName());
        if (set.size() > 127) {
            final List<String> list = new ArrayList<>(set);
            return list.subList(0, 127).toArray(new String[0]);
        } else return set.toArray(new String[0]);
    }

}
