package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Selector {

    private final String selector;
    private List<Entity> list;
    private CommandSender sender;

    protected Selector(String selector) {
        this.selector = selector;
    }

    protected boolean verify() {
        if (sender == null) {
            final Command<?>.Context context = Command.getContext();
            if (context != null && context.getSender() instanceof CommandSender sender) this.sender = sender;
            else sender = Bukkit.getConsoleSender();
        }
        try {
            this.list = Bukkit.selectEntities(sender, selector);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public Entity getEntity(CommandSender sender) {
        final List<Entity> list = this.getEntities(sender);
        if (list.size() > 0) return list.get(0);
        return null;
    }

    public @NotNull List<Entity> getEntities(CommandSender sender) {
        if (sender == this.sender && list != null) return list;
        return list = Bukkit.selectEntities(this.sender = sender, selector);
    }

}
