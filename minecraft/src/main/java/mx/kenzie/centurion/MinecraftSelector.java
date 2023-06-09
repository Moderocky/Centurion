package mx.kenzie.centurion;

import mx.kenzie.centurion.selector.Finder;
import mx.kenzie.centurion.selector.Selector;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class MinecraftSelector extends Selector<Entity> {

    protected final String selector;
    protected List<Entity> list;
    protected CommandSender sender;

    protected MinecraftSelector(String selector) {
        super(Finder.ALL_ENTITIES);
        this.selector = selector;
    }

    /**
     * @return an empty dummy selector
     */
    public static MinecraftSelector empty() {
        return new EmptySelector();
    }

    public static MinecraftSelector of(String key, List<Entity> entities) {
        return new PreSelector(key, entities);
    }

    public static MinecraftSelector of(String key, Entity... entities) {
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

    @Override
    public Entity getOne(CommandSender sender) {
        return this.getEntity(sender);
    }

    @Override
    public List<Entity> getAll(CommandSender sender) {
        return this.getEntities(sender);
    }

}

class EmptySelector extends MinecraftSelector {

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

class PreSelector extends MinecraftSelector {

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
