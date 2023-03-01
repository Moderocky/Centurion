package mx.kenzie.centurion;

import java.util.HashMap;
import java.util.Map;

public class PatternArgument extends HashedArg<ArgumentContainer> {
    public PatternArgument() { // todo argument argument ?
        super(ArgumentContainer.class);
        this.label = "pattern";
    }

    public static PatternArgument of(ArgumentContainer... containers) {
        return new PatternArgument() {
            private Map<String, ArgumentContainer> map;

            @Override
            protected Map<String, ArgumentContainer> getArguments() {
                if (map != null) return map;
                this.map = new HashMap<>();
                for (ArgumentContainer argument : containers)
                    this.map.put(argument.toString(), argument);
                return map;
            }
        };
    }

    public static PatternArgument of(Command<?> command) {
        return new PatternArgument() {
            private Map<String, ArgumentContainer> map;

            @Override
            protected Map<String, ArgumentContainer> getArguments() {
                if (map != null) return map;
                this.map = new HashMap<>();
                for (ArgumentContainer argument : command.behaviour.arguments)
                    this.map.put(argument.toString(), argument);
                return map;
            }
        };
    }

    @Override
    public boolean matches(String input) {
        if (lastHash == input.hashCode() && lastValue != null) return true;
        this.lastHash = input.hashCode();
        this.lastValue = this.parseNew(input);
        return (lastValue != null);
    }

    @Override
    public ArgumentContainer parseNew(String input) {
        final Command<?>.Context context = Command.getContext();
        if (context == null) return null;
        return this.getArguments().get(input);
    }

    protected Map<String, ArgumentContainer> getArguments() {
        final Map<String, ArgumentContainer> map = new HashMap<>();
        for (ArgumentContainer argument : Command.getContext().getCommand().behaviour.arguments)
            map.put(argument.toString(), argument);
        return map;
    }

    @Override
    public String[] possibilities() {
        final Command<?>.Context context = Command.getContext();
        if (context == null) return new String[0];
        return context.getCommand().behaviour.patterns;
    }

}
