package mx.kenzie.centurion;

import java.util.HashMap;
import java.util.Map;

public class ArgumentArgument extends TypedArgument<Argument> {

    protected final HashMap<String, Argument<?>> defaultArguments;

    public ArgumentArgument() {
        super(Argument.class);
        this.label = "argument";
        this.defaultArguments = new HashMap<>();
        this.registerDefault(Arguments.BOOLEAN);
        this.registerDefault(Arguments.DOUBLE);
        this.registerDefault(Arguments.LONG);
        this.registerDefault(Arguments.INTEGER);
        this.registerDefault(Arguments.STRING);
        this.registerDefault(Arguments.REGEX);
        this.registerDefault(Arguments.CLASS);
        this.registerDefault(Arguments.PATTERN);
        this.registerDefault(this);
    }

    public static ArgumentArgument of(Argument<?>... arguments) {
        return new ArgumentArgument() {
            private Map<String, Argument<?>> map;

            @Override
            protected Map<String, Argument<?>> getArguments() {
                if (map != null) return map;
                this.map = new HashMap<>();
                for (Argument<?> argument : arguments) map.put(argument.label(), argument);
                return map;
            }
        };
    }

    public static ArgumentArgument of(Command<?> command) {
        return new ArgumentArgument() {
            private Map<String, Argument<?>> map;

            @Override
            protected Map<String, Argument<?>> getArguments() {
                if (map != null) return map;
                this.map = new HashMap<>();
                for (ArgumentContainer argument : command.behaviour.arguments) this.addAll(argument, map);
                return map;
            }
        };
    }

    protected void registerDefault(Argument<?> argument) {
        this.defaultArguments.put(argument.label(), argument);
    }

    @Override
    public boolean matches(String input) {
        return this.getArguments().containsKey(input);
    }

    @Override
    public Argument<?> parse(String input) {
        return this.getArguments().get(input);
    }

    protected Map<String, Argument<?>> getArguments() {
        final Map<String, Argument<?>> map = new HashMap<>(defaultArguments);
        final Command<?>.Context context = Command.getContext();
        if (context == null) return map;
        final Command<?> command = Command.getContext().getCommand();
        if (command == null) return map;
        for (ArgumentContainer container : command.behaviour.arguments) this.addAll(container, map);
        return map;
    }

    protected void addAll(ArgumentContainer container, Map<String, Argument<?>> map) {
        for (Argument<?> argument : container.arguments) map.put(argument.label(), argument);
    }

    @Override
    public String[] possibilities() {
        if (possibilities != null && possibilities.length > 0) return possibilities;
        return this.getArguments().keySet().toArray(new String[0]);
    }

}
