package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
        } catch (Exception ignore) {
        }
        if (resolveNames) return (Type) Bukkit.getOfflinePlayer(input);
        else return (Type) Bukkit.getOfflinePlayerIfCached(input);
    }

    @Override
    public String[] possibilities() {
        if (possibilities.length > 0) return possibilities;
        if (Bukkit.getServer() == null) return new String[0];
        final Set<OfflinePlayer> set = new LinkedHashSet<>();
        set.addAll(Bukkit.getOnlinePlayers());
        set.addAll(Arrays.asList(Bukkit.getOfflinePlayers()));
        final List<String> list = new ArrayList<>(Math.min(128, set.size()));
        for (final OfflinePlayer player : set) list.add(player.getName());
        if (list.size() > 128) return list.subList(0, 127).toArray(new String[0]);
        else return list.toArray(new String[0]);
    }

}
