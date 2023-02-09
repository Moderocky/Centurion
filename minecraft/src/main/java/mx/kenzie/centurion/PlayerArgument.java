package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
}
