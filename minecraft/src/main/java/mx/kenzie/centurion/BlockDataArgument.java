package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.LinkedList;
import java.util.List;

class BlockDataArgument extends HashedArg<BlockData> {
    public BlockDataArgument() {
        super(BlockData.class);
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        try {
            return this.parseNew(input) != null;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public BlockData parseNew(String input) {
        if (Bukkit.getServer() == null) return null; // test only
        return lastValue = Bukkit.createBlockData(input.trim().toLowerCase());
    }

    @Override
    public String[] possibilities() {
        if (possibilities != null && possibilities.length > 0) return possibilities;
        final List<String> list = new LinkedList<>();
        for (Material value : Material.values()) {
            if (!value.isBlock()) continue;
            list.add(value.getKey().getKey());
        }
        return possibilities = list.toArray(new String[0]);
    }
}
