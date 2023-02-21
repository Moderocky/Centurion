package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String[] possibilities() {
        if (possibilities.length > 0) return possibilities;
        final List<String> list = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) list.add(world.getName());
        return list.toArray(new String[0]);
    }

}
