package mx.kenzie.centurion;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

public abstract class MinecraftCommand extends Command<CommandSender> implements TabCompleter, CommandExecutor {
    public static final EnumArgument<BlockFace> BLOCK_FACE = new EnumArgument<>(BlockFace.class);
    public static final EnumArgument<Material> MATERIAL = new EnumArgument<>(Material.class);
    public static final EnumArgument<EntityType> ENTITY_TYPE = new EnumArgument<>(EntityType.class);
    public static final TypedArgument<TextColor> COLOR = new ColorArgument().labelled("color");
    public static final TypedArgument<BlockData> BLOCK_DATA = new BlockDataArgument();
    public static final TypedArgument<Player> PLAYER = new PlayerArgument();
    public static final TypedArgument<Selector> SELECTOR = new SelectorArgument().labelled("entity");
    public static final TypedArgument<World> WORLD = new WorldArgument();
    public static final TypedArgument<NamespacedKey> KEY = new KeyArgument();
    public static final TypedArgument<RelativeNumber> RELATIVE_NUMBER = new RelativeNumberArgument(),
        LOCAL_NUMBER = new LocalNumberArgument();
    public static final Argument<Vector> VECTOR = new CompoundArgument<Vector>("vector")
        .arg(Arguments.DOUBLE.labelled("x"), Arguments.DOUBLE.labelled("y"), Arguments.DOUBLE.labelled("z"), arguments -> new Vector(arguments.<Double>get(0), arguments.get(1), arguments.get(2)))
        .arg(Arguments.DOUBLE, "meters", BLOCK_FACE.labelled("direction"), arguments -> arguments.<BlockFace>get(1).getDirection().multiply(arguments.<Double>get(0)));
    public static final Argument<Location> LOCATION = new CompoundArgument<Location>("location")
        .arg(VECTOR, "in", WORLD, arguments -> new Location(arguments.get(3), arguments.<Double>get(0), arguments.get(1), arguments.get(2)))
        .arg("spawn", "of", WORLD, arguments -> arguments.<World>get(0).getSpawnLocation())
        .arg("bed", "of", PLAYER, arguments -> arguments.<Player>get(0).getPotentialBedLocation());
    public static final Argument<RelativeVector> OFFSET = new CompoundArgument<RelativeVector>("offset")
        .arg(RELATIVE_NUMBER.labelled("x"), RELATIVE_NUMBER.labelled("y"), RELATIVE_NUMBER.labelled("z"), arguments -> new RelativeVector(arguments.get(0), arguments.get(1), arguments.get(2)))
        .arg(Arguments.DOUBLE, "meters", BLOCK_FACE.labelled("direction"), arguments -> RelativeVector.of(arguments.<BlockFace>get(1).getDirection().multiply(arguments.<Double>get(0))));
    public static final Argument<LocalVector> LOCAL_OFFSET = new CompoundArgument<LocalVector>("local")
        .arg(LOCAL_NUMBER.labelled("left"), LOCAL_NUMBER.labelled("up"), LOCAL_NUMBER.labelled("forwards"), arguments -> new LocalVector(arguments.get(0), arguments.get(1), arguments.get(2)));
    protected static final ColorProfile DEFAULT_PROFILE = new ColorProfile(NamedTextColor.WHITE, NamedTextColor.DARK_GREEN, NamedTextColor.GREEN, NamedTextColor.GOLD);

    static {
        ((CompoundArgument<Location>) LOCATION)
            .arg(OFFSET, "of", SELECTOR, arguments -> arguments.<Selector>get(2).getEntity(Command.<CommandSender>getContext().getSender()).getLocation().add(arguments.<BlockFace>get(1).getDirection().multiply(arguments.<Double>get(0))))
            .arg(OFFSET, "of", LOCATION, arguments -> arguments.<Location>get(2).add(arguments.<BlockFace>get(1).getDirection().multiply(arguments.<Double>get(0))));
    }

    protected String description, usage, permission;
    protected Component permissionMessage;

    {
        if (behaviour.lapse == Behaviour.DEFAULT_LAPSE) behaviour.lapse = this::printUsage;
    }

    protected MinecraftCommand(String description) {
        super();
        this.description = description;
        this.usage = '/' + behaviour.label;
        this.permission = null;
        this.permissionMessage = this.getPermissionMessage();
    }

    protected MinecraftCommand(String description, String usage, String permission, Component permissionMessage) {
        super();
        this.description = description;
        this.usage = usage;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
    }

    protected Component getPermissionMessage() {
        final ColorProfile profile = this.getProfile();
        return Component.textOfChildren(
            Component.text("!! ", profile.pop()),
            Component.translatable("commands.help.failed", profile.highlight())
        );
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
        sender.sendMessage(builder.build());
        return CommandResult.LAPSED;
    }

    protected Component print(ArgumentContainer container) {
        final TextComponent.Builder builder = Component.text();
        for (Argument<?> argument : container.arguments()) {
            builder.append(Component.space());
            builder.append(this.print(argument));
        }
        return builder.build();
    }

    protected Component print(Argument<?> argument) {
        final ColorProfile profile = this.getProfile();
        final TextComponent.Builder builder = Component.text();
        final boolean optional = argument.optional(), literal = argument.literal(), plural = argument.plural();
        if (optional) builder.append(Component.text('[', profile.pop()));
        else if (!literal) builder.append(Component.text('<', profile.pop()));
        builder.append(Component.text(argument.label(), profile.highlight()));
        if (plural) builder.append(Component.text("...", profile.dark()));
        if (optional) builder.append(Component.text(']', profile.pop()));
        else if (!literal) builder.append(Component.text('>', profile.pop()));
        return builder.build();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String... args) {
        final String input;
        if (args == null || args.length < 1) input = label;
        else input = label + " " + String.join(" ", args);
        final Result result = this.execute(sender, input);
        if (result.error() != null) {
            Bukkit.getLogger().log(Level.SEVERE, "Error in command: " + label + Arrays.toString(args));
            result.error().printStackTrace();
        }
        return result.successful();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String... args) {
        final List<String> options = new ArrayList<>();
        if (args.length < 1) for (ArgumentContainer argument : this.behaviour.arguments) {
            options.addAll(List.of(argument.arguments()[0].possibilities()));
        }
        else {
            final List<ArgumentContainer> containers = new LinkedList<>(this.behaviour.arguments);
            final String[] complete = new String[args.length - 1];
            final String current = args[args.length - 1].toLowerCase();
            System.arraycopy(args, 0, complete, 0, complete.length);
            final Iterator<ArgumentContainer> each = containers.iterator();
            arguments:
            while (each.hasNext()) {
                final ArgumentContainer next = each.next();
                final Argument<?>[] arguments = next.arguments();
                if (arguments.length <= complete.length) continue;
                int i = 0;
                for (; i < complete.length; i++) {
                    final String test = complete[i];
                    if (!arguments[i].matches(test)) continue arguments;
                }
                options.addAll(List.of(arguments[i].possibilities()));
            }
            final Iterator<String> iterator = options.iterator();
            while (iterator.hasNext()) {
                final String next = iterator.next();
                if (next.toLowerCase().startsWith(current)) continue;
                iterator.remove();
            }
        }
        return options;
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
