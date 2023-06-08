package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Selector {

    protected final String selector;
    protected List<Entity> list;
    protected CommandSender sender;

    protected Selector(String selector) {
        this.selector = selector;
    }

    /**
     * @return an empty dummy selector
     */
    public static Selector empty() {
        return new EmptySelector();
    }

    public static Selector of(String key, List<Entity> entities) {
        return new PreSelector(key, entities);
    }

    public static Selector of(String key, Entity... entities) {
        return new PreSelector(key, List.of(entities));
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

class EmptySelector extends Selector {

    protected EmptySelector() {
        super("@x");
    }

    @Override
    public @NotNull List<Entity> getEntities(CommandSender sender) {
        return Collections.emptyList();
    }

    @Override
    public Entity getEntity(CommandSender sender) {
        return null;
    }

    @Override
    protected boolean verify() {
        return true;
    }

}

class PreSelector extends Selector {

    protected PreSelector(String selector, List<Entity> list) {
        super(selector);
        this.list = list;
    }

    @Override
    protected boolean verify() {
        return list != null;
    }

    public @NotNull List<Entity> getEntities(CommandSender sender) {
        return list;
    }

}
