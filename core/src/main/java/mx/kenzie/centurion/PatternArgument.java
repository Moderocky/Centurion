package mx.kenzie.centurion;

import java.util.*;

public class PatternArgument extends HashedArg<ArgumentContainer> {
    protected Map<String, ArgumentContainer> model;

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
                return map = this.createMap(List.of(containers));
            }
        };
    }

    public static PatternArgument of(Command<?> command) {
        return new PatternArgument() {
            private Map<String, ArgumentContainer> map;

            @Override
            protected Map<String, ArgumentContainer> getArguments() {
                if (map != null) return map;
                return map = this.createMap(command.behaviour.arguments);
            }
        };
    }

    protected Map<String, ArgumentContainer> createMap(Collection<ArgumentContainer> arguments) {
        final Map<String, ArgumentContainer> map = new HashMap<>();
        for (ArgumentContainer argument : arguments) map.put(argument.toString().trim(), argument);
        return map;
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
        try {
            final Command<?>.Context context = Command.getContext();
            if (context == null) return null;
            return this.getArguments().get(input);
        } finally {
            this.model = null;
        }
    }

    @Override
    public ParseResult read(String input) {
        this.model = this.getArguments();
        for (String pattern : model.keySet()) {
            if (!input.startsWith(pattern)) continue;
            return new ParseResult(input.substring(0, pattern.length()), input.substring(pattern.length()));
        }
        return null;
    }

    protected Map<String, ArgumentContainer> getArguments() {
        if (model != null) return model;
        return this.createMap(Command.getContext().getCommand().behaviour.arguments);
    }

    @Override
    public String[] possibilities() {
        final Command<?>.Context context = Command.getContext();
        if (context == null) return new String[0];
        final Collection<ArgumentContainer> containers = context.getCommand().behaviour.arguments;
        final List<String> list = new ArrayList<>(containers.size());
        for (ArgumentContainer argument : containers) list.add(argument.toString().trim());
        return list.toArray(new String[0]);
    }

}
