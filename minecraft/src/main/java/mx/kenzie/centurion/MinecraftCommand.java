package mx.kenzie.centurion;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class MinecraftCommand extends Command<CommandSender> implements TabCompleter, CommandExecutor {
    protected static final ColorProfile DEFAULT_PROFILE = new ColorProfile(NamedTextColor.WHITE, NamedTextColor.DARK_GREEN, NamedTextColor.GREEN, NamedTextColor.GOLD);
    protected final String description, usage, permission;
    protected final Component permissionMessage;

    {
        this.behaviour.lapse = this::printUsage;
    }

    protected MinecraftCommand(String description) {
        super();
        this.description = description;
        this.usage = '/' + behaviour.label;
        this.permission = null;
        this.permissionMessage = null;
    }

    protected MinecraftCommand(String description, String usage, String permission, Component permissionMessage) {
        super();
        this.description = description;
        this.usage = usage;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
    }

    protected CommandResult printUsage(CommandSender sender, Arguments arguments) {
        final ColorProfile profile = this.getProfile();
        final TextComponent.Builder builder = Component.text();
        builder.append(Component.text("Usage for ", profile.dark()))
            .append(Component.text("/" + behaviour.label, profile.highlight()))
            .append(Component.text(":", profile.dark()));
        for (ArgumentContainer container : behaviour.arguments) {
            final Component hover;
            final ClickEvent click;
            if (container.hasInput()) {
                hover = Component.text("Click to Suggest");
                final StringBuilder text = new StringBuilder("/" + behaviour.label);
                for (Argument<?> argument : container.arguments()) {
                    if (!argument.literal()) break;
                    text.append(' ').append(argument.label());
                }
                click = ClickEvent.suggestCommand(text.toString());
            } else {
                hover = Component.text("Click to Run");
                click = ClickEvent.runCommand("/" + behaviour.label + container);
            }
            final Component line = Component.textOfChildren(
                Component.text("/", profile.pop()),
                Component.text(behaviour.label, profile.light()),
                this.print(container)
            ).hoverEvent(hover).clickEvent(click);
            builder.append(Component.newline()).append(Component.text("  "));
            builder.append(line);
        }
        return CommandResult.PASSED;
    }

    private Component print(ArgumentContainer container) {
        final ColorProfile profile = this.getProfile();
        final TextComponent.Builder builder = Component.text();
        for (Argument<?> argument : container.arguments()) {
            builder.append(Component.space());
            final boolean optional = argument.optional(), literal = argument.literal(), plural = argument.plural();
            if (optional) builder.append(Component.text('[', profile.pop()));
            else if (!literal) builder.append(Component.text('<', profile.pop()));
            builder.append(Component.text(argument.label(), profile.highlight()));
            if (plural) builder.append(Component.text("...", profile.dark()));
            if (optional) builder.append(Component.text(']', profile.pop()));
            else if (!literal) builder.append(Component.text('>', profile.pop()));
        }
        return builder.build();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String... args) {
        final String input;
        if (args == null || args.length < 1) input = label;
        else input = label + " " + String.join(" ", args);
        final Result result = this.execute(sender, input);
        return result.successful();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String... args) {

        return null;
    }

    @SuppressWarnings("deprecation")
    public void register(Plugin plugin) {
        try {
            final Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            if (!constructor.isAccessible()) constructor.setAccessible(true);
            final PluginCommand command = constructor.newInstance(behaviour.label, plugin);
            this.update(command);
            command.register(this.getCommandMap());
            if (this.getCommandMap().register(behaviour.label, plugin.getName(), command)) {
                command.setExecutor(this);
                command.setTabCompleter(this);
            } else {
                final org.bukkit.command.Command current = this.getCommandMap().getCommand(command.getName());
                if (current instanceof PluginCommand found) {
                    this.update(found);
                    found.setExecutor(this);
                    found.setTabCompleter(this);
                }
                Bukkit.getLogger().log(Level.WARNING, "A command '/" + behaviour.label + "' is already defined!");
                Bukkit.getLogger().log(Level.WARNING, "As this cannot be replaced, the executor will be overridden.");
                Bukkit.getLogger()
                    .log(Level.WARNING, "To avoid this warning, please do not add MinecraftCommands to your plugin.yml.");
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void update(org.bukkit.command.Command command) {
        command.setAliases(new ArrayList<>(behaviour.aliases));
        command.setDescription(this.description);
        command.setPermission(this.permission);
        command.permissionMessage(this.permissionMessage);
        command.setUsage(this.usage);
    }

    /**
     * This can be overridden if Bukkit removes or changes the method.
     */
    protected CommandMap getCommandMap() {
        return Bukkit.getCommandMap();
    }

    /**
     * This can be overridden to change colours in default messages.
     */
    protected ColorProfile getProfile() {
        return DEFAULT_PROFILE;
    }

}
