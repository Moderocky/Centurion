package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.World;

class WorldArgument extends TypedArgument<World> {
    public WorldArgument() {
        super(World.class);
    }

    @Override
    public boolean matches(String input) {
        return Bukkit.getWorld(input) != null;
    }

    @Override
    public World parse(String input) {
        return Bukkit.getWorld(input);
    }
}
