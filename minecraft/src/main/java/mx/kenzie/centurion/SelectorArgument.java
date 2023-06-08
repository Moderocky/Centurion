package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class SelectorArgument extends HashedArg<MinecraftSelector> {

    public SelectorArgument() {
        super(MinecraftSelector.class);
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        this.lastValue = null;
        try {
            final MinecraftSelector selector = this.parseNew(input);
            if (!selector.verify()) return false;
            this.lastValue = selector;
            return true;
        } catch (IllegalArgumentException ex) {
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
