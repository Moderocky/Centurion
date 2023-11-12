package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDArgument extends HashedArg<UUID> {

    public UUIDArgument() {
        super(UUID.class);
        this.label = "uuid";
    }

    @Override
    public boolean matches(String input) {
        if (input.length() > 36) return false;
        try {
            this.lastValue = this.parseNew(input);
            this.lastHash = input.hashCode();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public UUID parseNew(String input) {
        return UUID.fromString(input);
    }

    @Override
    public String[] possibilities() {
        if (possibilities != null && possibilities.length > 0) return possibilities;
        final List<String> list = new ArrayList<>();
        for (final Player player : Bukkit.getOnlinePlayers()) list.add(player.getUniqueId().toString());
        return list.toArray(new String[0]);
    }

}
