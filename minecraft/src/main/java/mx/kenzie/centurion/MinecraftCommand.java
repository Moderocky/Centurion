package mx.kenzie.centurion;

import mx.kenzie.centurion.selector.Criterion;
import mx.kenzie.centurion.selector.Finder;
import mx.kenzie.centurion.selector.Selector;
import mx.kenzie.centurion.selector.Universe;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public abstract class MinecraftCommand extends Command<CommandSender> implements TabCompleter, CommandExecutor {

    public static final EnumArgument<BlockFace> BLOCK_FACE = new EnumArgument<>(BlockFace.class);
    public static final EnumArgument<Material> MATERIAL = new EnumArgument<>(Material.class);
    public static final EnumArgument<EntityType> ENTITY_TYPE = new EnumArgument<>(EntityType.class);
    public static final EnumArgument<GameMode> GAME_MODE = new EnumArgument<>(GameMode.class);
    public static final TypedArgument<TextColor> COLOR = new ColorArgument().labelled("color");
    public static final TypedArgument<BlockData> BLOCK_DATA = new BlockDataArgument();
    public static final TypedArgument<Player> PLAYER = new PlayerArgument();
    public static final TypedArgument<OfflinePlayer> KNOWN_PLAYER = new ResolvablePlayerArgument<>(false),
        ANY_PLAYER = new ResolvablePlayerArgument<>(true);
    public static final TypedArgument<Predicate<Double>> INEQUALITY = new InequalityArgument();
    public static final TypedArgument<Selector<Entity>> SELECTOR = new MinecraftSelectorArgument().labelled("entity");
    public static final TypedArgument<World> WORLD = new WorldArgument();
    public static final TypedArgument<UUID> UUID = new UUIDArgument();
    public static final TypedArgument<NamespacedKey> KEY = new KeyArgument();
    public static final TypedArgument<Tag<Material>> MATERIAL_TAG = TagArgument.materials();
    public static final TypedArgument<Tag<Material>> ITEM_TAG = TagArgument.items();
    public static final TypedArgument<Tag<EntityType>> ENTITY_TAG = TagArgument.entities();
    public static final TypedArgument<RelativeNumber> RELATIVE_NUMBER = new RelativeNumberArgument(), LOCAL_NUMBER = new LocalNumberArgument();
    public static final TypedArgument<Vector> VECTOR = new CompoundArgument<>("vector", Vector.class).arg(
            Arguments.DOUBLE.labelled("x"), Arguments.DOUBLE.labelled("y"), Arguments.DOUBLE.labelled("z"),
            arguments -> new Vector(arguments.<Double>get(0), arguments.get(1), arguments.get(2)))
        .arg(Arguments.DOUBLE, "meters", BLOCK_FACE.labelled("direction"),
            arguments -> arguments.<BlockFace>get(1).getDirection().multiply(arguments.<Double>get(0)))
        .described("An x/y/z vector.");
    public static final TypedArgument<Location> LOCATION = new CompoundArgument<>("location", Location.class).arg(
            VECTOR, "in", WORLD,
            arguments -> new Location(arguments.get(3), arguments.<Double>get(0), arguments.get(1), arguments.get(2)))
        .arg("spawn", "of", WORLD, arguments -> arguments.<World>get(0).getSpawnLocation())
        .arg("bed", "of", PLAYER, arguments -> arguments.<Player>get(0).getPotentialBedLocation())
        .arg("target", "block", arguments -> {
            if (MinecraftCommand.getContextSender() instanceof Player player) {
                final Block block = player.getTargetBlockExact(32);
                if (block != null) return block.getLocation().toCenterLocation();
                return player.getLocation();
            }
            return null;
        })
        .arg("target", "entity", arguments -> {
            if (MinecraftCommand.getContextSender() instanceof Player player) {
                final Entity entity = player.getTargetEntity(32);
                if (entity != null) return entity.getLocation();
                return player.getLocation();
            }
            return null;
        })
        .described("A fixed position in a world.");
    public static final TypedArgument<RelativeVector> OFFSET = new CompoundArgument<>("offset", RelativeVector.class) {
        @Override
        public String[] possibilities() {
            if (MinecraftCommand.getContextSender() instanceof Player player) {
                final Block block = player.getTargetBlockExact(5);
                if (block != null) return new String[]{
                    "~ ~ ~",
                    (block.getX() + " " + block.getY() + " " + block.getZ())
                };
            }
            return new String[]{"~ ~ ~"};
        }
    }
        .arg(RELATIVE_NUMBER.labelled("x"), RELATIVE_NUMBER.labelled("y"), RELATIVE_NUMBER.labelled("z"),
            arguments -> new RelativeVector(arguments.get(0), arguments.get(1), arguments.get(2)))
        .arg(Arguments.DOUBLE, "meters", BLOCK_FACE.labelled("direction"), arguments -> RelativeVector.of(
            arguments.<BlockFace>get(1).getDirection().multiply(arguments.<Double>get(0))))
        .described("An x/y/z offset from a location.");
    public static final TypedArgument<LocalVector> LOCAL_OFFSET = new CompoundArgument<>("local",
        LocalVector.class).arg(LOCAL_NUMBER.labelled("left"), LOCAL_NUMBER.labelled("up"),
            LOCAL_NUMBER.labelled("forwards"),
            arguments -> new LocalVector(arguments.get(0), arguments.get(1), arguments.get(2)))
        .described("A directional offset from a location.");
    public static final ColorProfile DEFAULT_PROFILE = new ColorProfile(NamedTextColor.WHITE,
        NamedTextColor.DARK_GREEN, NamedTextColor.GREEN, NamedTextColor.GOLD);
    protected static final Universe<Entity> ENTITY_UNIVERSE = Universe.of(Finder.ALL_ENTITIES, Finder.ALL_PLAYERS,
        Finder.PLAYER, Finder.RANDOM_PLAYER, Finder.SENDER, Criterion.ENTITY_DISTANCE, Criterion.ENTITY_TYPE,
        Criterion.LIMIT, Criterion.GAME_MODE, Criterion.LEVEL, Criterion.X_ROTATION, Criterion.Y_ROTATION);

    static {
        ((CompoundArgument<Location>) LOCATION)
            .arg(OFFSET, "of", SELECTOR, arguments -> {
                final RelativeVector vector = arguments.get(0);
                final MinecraftSelector selector = arguments.get(1);
                final Location start = selector.getEntity(Command.getContextSender()).getLocation();
                return vector.relativeTo(start);
            })
            .arg(OFFSET, "of", LOCATION,
                arguments -> arguments.<RelativeVector>get(0).relativeTo(arguments.<Location>get(1)));
    }

    protected String usage;
    protected Permission permission;
    protected Component permissionMessage;

    {
        if (behaviour.lapse == Behaviour.DEFAULT_LAPSE) behaviour.lapse = this::printUsage;
    }

    protected MinecraftCommand() {
        this("Unknown command.");
    }

    protected MinecraftCommand(String description) {
        super();
        this.description = description;
        this.usage = '/' + behaviour.label;
        this.permissionMessage = this.getPermissionMessage();
    }

    protected MinecraftCommand(String description, String usage, String permission, Component permissionMessage) {
        super();
        this.description = description;
        this.usage = usage;
        this.permission = new Permission(permission, PermissionDefault.OP);
        this.permissionMessage = permissionMessage;
    }

    @Override
    public abstract MinecraftBehaviour create();

    protected Component getPermissionMessage() {
        final ColorProfile profile = this.getProfile();
        return Component.textOfChildren(text("!! ", profile.pop()),
            Component.translatable("commands.help.failed", "Unknown command or insufficient permissions.",
                profile.highlight()));
    }

    protected CommandResult printUsage(CommandSender sender, Arguments arguments) {
        //<editor-fold desc="Print All Patterns" defaultstate="collapsed">
        final boolean tearDown = Command.getContext() == null;
        if (tearDown) Command.setContext(new Context(sender, ""));
        final ColorProfile profile = this.getProfile();
        final TextComponent.Builder builder = text();
        builder.append(translatable("centurion.text.usage", "Usage for %s:", profile.dark()).args(
            text("/" + behaviour.label, profile.highlight())));
        for (ArgumentContainer container : behaviour.arguments) {
            if (!behaviour.canExecute(sender, container)) continue;
            final Component hover;
            final ClickEvent click;
            if (container.hasInput()) {
                final String description = behaviour.descriptions.get(container);
                if (description != null) hover = Component.textOfChildren(
                    text(description, profile.light()),
                    Component.newline(),
                    translatable("centurion.tooltip.suggest", "Click to Suggest", profile.highlight())
                );
                else hover = translatable("centurion.tooltip.suggest", "Click to Suggest", profile.highlight());
                final StringBuilder text = new StringBuilder("/" + behaviour.label);
                for (Argument<?> argument : container.arguments()) {
                    if (!argument.literal()) break;
                    text.append(' ').append(argument.label());
                }
                click = ClickEvent.suggestCommand(text.toString());
            } else {
                final String description = behaviour.descriptions.get(container);
                if (description != null) hover = Component.textOfChildren(
                    text(description, profile.light()),
                    Component.newline(),
                    translatable("centurion.tooltip.run", "Click to Run", profile.highlight())
                );
                else hover = translatable("centurion.tooltip.run", "Click to Run", profile.highlight());
                click = ClickEvent.runCommand("/" + behaviour.label + container);
            }
            final Component line = Component.textOfChildren(text("/", profile.pop()),
                text(behaviour.label, profile.light()), this.print(container, 0)).hoverEvent(hover).clickEvent(click);
            builder.append(Component.newline()).append(text("  "));
            builder.append(line);
        }
        sender.sendMessage(builder.build());
        if (tearDown) Command.setContext(null);
        return CommandResult.LAPSED;
        //</editor-fold>
    }

    protected Component print(ArgumentContainer container, int step) {
        if (Command.getContext() != null) Command.getContext().nestCounter = 0;
        final TextComponent.Builder builder = text();
        for (Argument<?> argument : container.arguments()) {
            builder.append(Component.space());
            builder.append(this.print(argument, step));
        }
        return builder.build();
    }

    protected Component print(Argument<?> argument, int step) {
        //<editor-fold desc="Print [argument...]" defaultstate="collapsed">
        final ColorProfile profile = this.getProfile();
        final TextComponent.Builder builder = text();
        final boolean optional = argument.optional(), literal = argument.literal(), plural = argument.plural();
        final String label = argument.label();
        final String[] possibilities = argument.possibilities();
        if (optional) builder.append(text('[', profile.pop()));
        else if (!literal) builder.append(text('<', profile.pop()));
        if (argument instanceof CompoundArgument<?>) builder.append(text('*', profile.pop()));
        if (possibilities.length > 0) builder.append(text(label, profile.highlight()).insertion(possibilities[0]));
        else builder.append(translatable("centurion.argument." + label, label, profile.highlight()));
        if (plural) builder.append(text("...", profile.dark()));
        if (optional) builder.append(text(']', profile.pop()));
        else if (!literal) builder.append(text('>', profile.pop()));
        final Component component = builder.build();
        if (argument instanceof CompoundArgument<?> compound && step < 2) return this.print(compound, component, step);
        if (argument.description() == null) return component;
        return component.hoverEvent(text(argument.description()));
        //</editor-fold>
    }

    private Component print(CompoundArgument<?> argument, Component component, int step) {
        final TextComponent.Builder builder = text();
        if (argument.description() != null) builder.append(text(argument.description()));
        for (CompoundArgument.InnerContainer container : argument.arguments) {
            builder.append(Component.newline());
            builder.append(this.print(container, step + 1));
        }
        return component.hoverEvent(builder.build());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String... args) {
        final String input;
        if (args == null || args.length < 1) input = behaviour.label;
        else input = behaviour.label + " " + String.join(" ", args);
        // can't use the given label here in case it is plugin:alias
        final Result result = this.execute(sender, input);
        if (result.error() != null) {
            Bukkit.getLogger().log(Level.SEVERE, "Error in command: " + label + Arrays.toString(args));
            result.error().printStackTrace();
        }
        return result.successful();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String... args) {
        //<editor-fold desc="Tab Completion Options" defaultstate="collapsed">
        final List<String> options = new ArrayList<>();
        Command.setContext(new Context(sender, String.join(" ", args)));
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
                if (!behaviour.canExecute(sender, next)) continue;
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
        Command.setContext(null);
        return options;
        //</editor-fold>
    }

    @SuppressWarnings("deprecation")
    public void register(Plugin plugin) {
        //<editor-fold desc="Register with Command Map" defaultstate="collapsed">
        try {
            final Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class,
                Plugin.class);
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
                Bukkit.getLogger().log(Level.WARNING,
                    "To avoid this warning, please do not add MinecraftCommands to your plugin.yml.");
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        if (behaviour instanceof MinecraftBehaviour behaviour) {
            final PluginManager manager = Bukkit.getPluginManager();
            for (Permission value : behaviour.permissions.values()) {
                if (manager.getPermission(value.getName()) != null) continue;
                manager.addPermission(value);
            }
            if (permission == null) return;
            if (manager.getPermission(permission.getName()) != null) return;
            manager.addPermission(permission);
        }
        //</editor-fold>
    }

    private void update(org.bukkit.command.Command command) {
        command.setAliases(new ArrayList<>(behaviour.aliases));
        command.setDescription(this.description);
        command.setPermission(permission != null ? permission.getName() : null);
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

    @Override
    protected MinecraftBehaviour command(String label, String... aliases) {
        return new MinecraftBehaviour(label, aliases);
    }

    public MinecraftBehaviour behaviour() {
        return (MinecraftBehaviour) behaviour;
    }

    public class MinecraftBehaviour extends Behaviour {

        protected Map<ArgumentContainer, Permission> permissions = new LinkedHashMap<>();

        protected MinecraftBehaviour(String label, String... aliases) {
            super(label, aliases);
        }

        @Override
        public MinecraftBehaviour arg(Object arg1, Input<CommandSender> function) {
            return this.arg(List.of(arg1), function);
        }

        @Override
        public MinecraftBehaviour arg(Object arg1, Object arg2, Input<CommandSender> function) {
            return this.arg(List.of(arg1, arg2), function);
        }

        @Override
        public MinecraftBehaviour arg(Object arg1, Object arg2, Object arg3, Input<CommandSender> function) {
            return this.arg(List.of(arg1, arg2, arg3), function);
        }

        @Override
        public MinecraftBehaviour arg(Object arg1, Object arg2, Object arg3, Object arg4, Input<CommandSender> function) {
            return this.arg(List.of(arg1, arg2, arg3, arg4), function);
        }

        @Override
        public MinecraftBehaviour arg(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Input<CommandSender> function) {
            return this.arg(List.of(arg1, arg2, arg3, arg4, arg5), function);
        }

        @Override
        public MinecraftBehaviour arg(Collection<Object> arguments, Input<CommandSender> function) {
            return (MinecraftBehaviour) super.arg(arguments, function);
        }

        @Override
        public MinecraftBehaviour passAllArguments() {
            return (MinecraftBehaviour) super.passAllArguments();
        }

        @Override
        public MinecraftBehaviour lapse(EmptyInput<CommandSender> function) {
            return (MinecraftBehaviour) super.lapse(function);
        }

        @Override
        public MinecraftBehaviour description(String description) {
            return (MinecraftBehaviour) super.description(description);
        }

        public MinecraftBehaviour permission(PermissionProvider provider) {
            return this.permission(provider.qualifiedName(), provider.getDefault());
        }

        public MinecraftBehaviour permission(String permission) {
            return this.permission(permission, PermissionDefault.OP);
        }

        public MinecraftBehaviour permission(String permission, PermissionDefault allow) {
            return this.permission(new Permission(permission, allow));
        }

        public MinecraftBehaviour permission(Permission permission) {
            if (previous == null) MinecraftCommand.this.permission = permission;
            else this.permissions.put(previous, permission);
            return this;
        }

        @Override
        public boolean canExecute(CommandSender sender, ArgumentContainer container) {
            if (permissions.isEmpty()) return true;
            final Permission permission = permissions.get(container);
            if (permission == null) return true;
            return sender.hasPermission(permission);
        }

    }

}
