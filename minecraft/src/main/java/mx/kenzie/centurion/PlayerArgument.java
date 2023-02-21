package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class PlayerArgument extends TypedArgument<Player> {
    public PlayerArgument() {
        super(Player.class);
    }

    @Override
    public boolean matches(String input) {
        return Bukkit.getPlayer(input) != null;
    }

    @Override
    public Player parse(String input) {
        return Bukkit.getPlayer(input);
    }

    @Override
    public String[] possibilities() {
        if (possibilities.length > 0) return possibilities;
        final List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) list.add(player.getName());
        return list.toArray(new String[0]);
    }

}
